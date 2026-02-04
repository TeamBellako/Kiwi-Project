package com.bellako.kiwi

import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.quests.data.QuestDataMapper
import com.bellako.kiwi.features.quests.data.SubquestStatus
import com.bellako.kiwi.features.quests.model.IQuestsAPI
import com.bellako.kiwi.features.quests.model.QuestsRepository
import com.bellako.kiwi.features.quests.model.QuestsViewModel
import com.bellako.kiwi.features.quests.tests.QuestsTestFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class QuestsIntegrationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var api: IQuestsAPI
    private lateinit var repository: QuestsRepository
    private lateinit var viewModel: QuestsViewModel

    @Before
    fun setUp() {
        api = mock(IQuestsAPI::class.java)
        repository = QuestsRepository(api)
        viewModel = QuestsViewModel(repository)
    }

    // -------------------------------------------------------------------------
    // Load active quests
    // -------------------------------------------------------------------------

    @Test
    fun `load active quests successfully`() =
        runTest(mainDispatcherRule.dispatcher) {
            val questsDomain = QuestsTestFactory.validQuestsState().quests
            val questsDto = questsDomain.map { QuestDataMapper.toDto(it) }

            whenever(api.getActiveQuests()).thenReturn(questsDto)

            viewModel.loadActiveQuests()
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(2, state.quests.size)
        }

    @Test
    fun `load active quests with server error`() =
        runTest(mainDispatcherRule.dispatcher) {
            doThrow(createFakeHttpException(HTTP_INTERNAL_ERROR))
                .whenever(api)
                .getActiveQuests()

            viewModel.loadActiveQuests()
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertTrue(uiState is com.bellako.kiwi.common.data.UIState.GeneralError)
        }

    // -------------------------------------------------------------------------
    // Give quest
    // -------------------------------------------------------------------------

    @Test
    fun `give quest adds quest to state and emits notification`() =
        runTest(mainDispatcherRule.dispatcher) {
            val quest = QuestsTestFactory.questWithThreeSubquests()
            val questDto = QuestDataMapper.toDto(quest)

            whenever(api.giveQuest(quest.id)).thenReturn(questDto)

            val notificationDeferred =
                async {
                    viewModel.getNotifications().first()
                }

            viewModel.giveQuest(quest.id)
            advanceUntilIdle()

            val notification = notificationDeferred.await()
            assertTrue(notification is QuestNotificationEvent.NewQuest)
        }

    // -------------------------------------------------------------------------
    // Complete subquest
    // -------------------------------------------------------------------------

    @Test
    fun `complete subquest emits subquest completed`() =
        runTest(mainDispatcherRule.dispatcher) {
            val quest = QuestsTestFactory.questWithThreeSubquests()
            val questDto = QuestDataMapper.toDto(quest)

            whenever(api.getActiveQuests()).thenReturn(listOf(questDto))
            whenever(api.completeSubquest(any())).thenReturn(questDto)

            viewModel.loadActiveQuests()
            advanceUntilIdle()

            val notificationDeferred =
                async {
                    viewModel.getNotifications().first()
                }

            viewModel.completeSubquest(3)
            advanceUntilIdle()

            val notification = notificationDeferred.await()
            assertTrue(notification is QuestNotificationEvent.SubquestCompleted)
        }

    // -------------------------------------------------------------------------
    // Fail subquest
    // -------------------------------------------------------------------------

    @Test
    fun `fail subquest updates state and emits SubquestFailed`() =
        runTest(mainDispatcherRule.dispatcher) {
            val quest = QuestsTestFactory.questWithThreeSubquests()
            val initialDto = QuestDataMapper.toDto(quest)

            val failedQuestDto =
                initialDto.copy(
                    subquests =
                        initialDto.subquests.map {
                            if (it.subquestId == 3) {
                                it.copy(status = SubquestStatus.FAILED.toString())
                            } else {
                                it
                            }
                        },
                )

            whenever(api.getActiveQuests()).thenReturn(listOf(initialDto))
            whenever(api.failSubquest(any())).thenReturn(failedQuestDto)

            viewModel.loadActiveQuests()
            advanceUntilIdle()

            val notificationDeferred =
                async { viewModel.getNotifications().first() }

            viewModel.failSubquest(3)
            advanceUntilIdle()

            val state = viewModel.state.value
            val updatedQuest = state.quests.first()

            assertTrue(
                updatedQuest.subquests.any {
                    it.id == 3 && it.status == SubquestStatus.FAILED
                },
            )

            val notification = notificationDeferred.await()
            assertTrue(notification is QuestNotificationEvent.SubquestFailed)
        }
}
