package com.bellako.kiwi

import com.bellako.kiwi.features.conversations.model.ConversationViewModel
import com.bellako.kiwi.features.conversations.model.ConversationsRepository
import com.bellako.kiwi.features.conversations.tests.ConversationsTestFactory
import com.bellako.kiwi.features.incidences.model.UserIncidenceManager
import com.bellako.kiwi.features.personality.model.PersonalityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ConversationViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ConversationsRepository
    private lateinit var viewModel: ConversationViewModel

    @Before
    fun setUp() {
        repository = mock(ConversationsRepository::class.java)
        viewModel =
            ConversationViewModel(
                repository,
                mock(PersonalityRepository::class.java),
                mock(UserIncidenceManager::class.java),
            )
    }

    // -------------------------------------------------------------------------
    // start
    // -------------------------------------------------------------------------

    @Test
    fun `start loads conversation and sets isVisible to true`() =
        runTest(mainDispatcherRule.dispatcher) {
            val conversation = ConversationsTestFactory.validConversationDomain(id = 1L)
            whenever(repository.getById(1L)).thenReturn(conversation)

            viewModel.start(1L)
            advanceUntilIdle()

            assertEquals(conversation, viewModel.active.value)
            assertTrue(viewModel.isVisible.value)
        }

    @Test
    fun `start with unknown id does not crash and leaves state unchanged`() =
        runTest(mainDispatcherRule.dispatcher) {
            org.mockito.Mockito
                .doAnswer { throw IOException("Not found") }
                .whenever(repository)
                .getById(99L)

            viewModel.start(99L)
            advanceUntilIdle()

            assertNull(viewModel.active.value)
            assertFalse(viewModel.isVisible.value)
        }

    // -------------------------------------------------------------------------
    // next (sin opciones)
    // -------------------------------------------------------------------------

    @Test
    fun `next with END conversation ends the conversation`() =
        runTest(mainDispatcherRule.dispatcher) {
            val conversation = ConversationsTestFactory.endConversationDomain()
            whenever(repository.getById(conversation.id ?: 2L)).thenReturn(conversation)

            viewModel.start(conversation.id ?: 2L)
            advanceUntilIdle()

            viewModel.next()
            advanceUntilIdle()

            assertFalse(viewModel.isVisible.value)
        }

    @Test
    fun `next with CONVERSATION nextEvent loads next conversation`() =
        runTest(mainDispatcherRule.dispatcher) {
            val firstConversation = ConversationsTestFactory.chainedConversationDomain() // nextEvent=CONVERSATION, eventId=2L
            val secondConversation = ConversationsTestFactory.endConversationDomain() // id=2L

            whenever(repository.getById(1L)).thenReturn(firstConversation)
            whenever(repository.getById(2L)).thenReturn(secondConversation)

            viewModel.start(1L)
            advanceUntilIdle()

            viewModel.next()
            advanceUntilIdle()

            assertEquals(secondConversation, viewModel.active.value)
            assertTrue(viewModel.isVisible.value)
        }

    @Test
    fun `next does nothing when there is no active conversation`() =
        runTest(mainDispatcherRule.dispatcher) {
            viewModel.next()
            advanceUntilIdle()

            assertNull(viewModel.active.value)
            assertFalse(viewModel.isVisible.value)
        }

    // -------------------------------------------------------------------------
    // next (con opción)
    // -------------------------------------------------------------------------

    @Test
    fun `next with option registers selected option id`() =
        runTest(mainDispatcherRule.dispatcher) {
            val conversation = ConversationsTestFactory.conversationWithOptions() // id=3L
            val targetConversation = ConversationsTestFactory.optionTargetConversationDomain() // id=4L, END

            whenever(repository.getById(3L)).thenReturn(conversation)
            whenever(repository.getById(4L)).thenReturn(targetConversation)

            viewModel.start(3L)
            advanceUntilIdle()

            val chosenOption = conversation.options.first() // id=10L, nextEventId=4L
            viewModel.next(chosenOption)
            advanceUntilIdle()

            assertTrue(viewModel.selectedOptions.value.contains(10L))
        }

    @Test
    fun `next with multiple options accumulates all selected ids`() =
        runTest(mainDispatcherRule.dispatcher) {
            // conv1 (id=3L): opción 10L navega a conv2 (id=4L)
            val conv1 = ConversationsTestFactory.conversationWithOptions()
            // conv2 (id=4L): opción 11L navega a endConv (id=2L, END)
            val conv2 =
                ConversationsTestFactory.conversationWithOptions().copy(
                    id = 4L,
                    options =
                        listOf(
                            ConversationsTestFactory.validConversationOption(id = 11L, nextEventId = 2L),
                        ),
                )
            val endConv = ConversationsTestFactory.endConversationDomain() // id=2L, END

            whenever(repository.getById(3L)).thenReturn(conv1)
            whenever(repository.getById(4L)).thenReturn(conv2)
            whenever(repository.getById(2L)).thenReturn(endConv)

            viewModel.start(3L)
            advanceUntilIdle()

            viewModel.next(conv1.options[0]) // selecciona 10L, navega a 4L
            advanceUntilIdle()

            viewModel.next(conv2.options[0]) // selecciona 11L, navega a 2L (END → end())
            advanceUntilIdle()

            val selected = viewModel.selectedOptions.value
            assertTrue(selected.contains(10L))
            assertTrue(selected.contains(11L))
            assertEquals(2, selected.size)
        }

    // -------------------------------------------------------------------------
    // end
    // -------------------------------------------------------------------------

    @Test
    fun `end hides conversation and clears active`() =
        runTest(mainDispatcherRule.dispatcher) {
            val conversation = ConversationsTestFactory.validConversationDomain()
            whenever(repository.getById(1L)).thenReturn(conversation)

            viewModel.start(1L)
            advanceUntilIdle()

            viewModel.end()
            advanceUntilIdle()

            assertFalse(viewModel.isVisible.value)
            assertNull(viewModel.active.value)
        }

    @Test
    fun `end saves accumulated options via repository`() =
        runTest(mainDispatcherRule.dispatcher) {
            // conv1 (id=3L, CONVERSATION): opción 10L navega a conv2 (id=4L)
            val conv1 = ConversationsTestFactory.conversationWithOptions()
            // conv2 (id=4L, END): opción 11L dispara end() automático al seleccionarla
            val conv2 =
                ConversationsTestFactory.conversationWithOptions().copy(
                    id = 4L,
                    nextEvent = com.bellako.kiwi.features.conversations.data.NextEventType.END,
                    options =
                        listOf(
                            ConversationsTestFactory.validConversationOption(id = 11L, nextEventId = 0L),
                        ),
                )

            whenever(repository.getById(3L)).thenReturn(conv1)
            whenever(repository.getById(4L)).thenReturn(conv2)

            viewModel.start(3L)
            advanceUntilIdle()

            viewModel.next(conv1.options[0]) // selecciona 10L, navega a 4L
            advanceUntilIdle()

            viewModel.next(conv2.options[0]) // selecciona 11L, conv2.nextEvent=END → end() automático
            advanceUntilIdle()

            verify(repository).saveOptions(listOf(10L, 11L))
        }

    @Test
    fun `end clears selectedOptions after finishing`() =
        runTest(mainDispatcherRule.dispatcher) {
            // conv1 (id=3L, CONVERSATION): opción 10L navega a targetConv (id=4L)
            val conv1 = ConversationsTestFactory.conversationWithOptions()
            // targetConv (id=4L, END): al llamar next() dispara end() automático
            val targetConv = ConversationsTestFactory.optionTargetConversationDomain() // id=4L, END

            whenever(repository.getById(3L)).thenReturn(conv1)
            whenever(repository.getById(4L)).thenReturn(targetConv)

            viewModel.start(3L)
            advanceUntilIdle()

            viewModel.next(conv1.options[0]) // selecciona 10L, navega a 4L
            advanceUntilIdle()

            viewModel.next() // targetConv.nextEvent=END → end() automático
            advanceUntilIdle()

            // end() limpió selectedOptions
            assertTrue(viewModel.selectedOptions.value.isEmpty())
        }

    @Test
    fun `end does not call saveOptions when no options were selected`() =
        runTest(mainDispatcherRule.dispatcher) {
            val conversation = ConversationsTestFactory.validConversationDomain()
            whenever(repository.getById(1L)).thenReturn(conversation)

            viewModel.start(1L)
            advanceUntilIdle()

            viewModel.end()
            advanceUntilIdle()

            // saveOptions no debe llamarse si la lista está vacía
            verify(repository, never()).saveOptions(any())
        }
}
