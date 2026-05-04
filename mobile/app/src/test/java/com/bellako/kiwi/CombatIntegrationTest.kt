package com.bellako.kiwi

import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.combat.data.CombatDataMapper
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.combat.model.CombatRepository
import com.bellako.kiwi.features.combat.model.CombatViewModel
import com.bellako.kiwi.features.combat.model.ICombatAPI
import com.bellako.kiwi.features.combat.tests.CombatTestFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CombatIntegrationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var api: ICombatAPI
    private lateinit var repository: CombatRepository
    private lateinit var viewModel: CombatViewModel

    @Before
    fun setUp() {
        api = mock(ICombatAPI::class.java)
        repository = CombatRepository(api)
        viewModel = CombatViewModel(repository, RuntimeEnvironment.getApplication())
    }

    // -------------------------------------------------------------------------
    // start
    // -------------------------------------------------------------------------

    @Test
    fun `start loads combat into active state`() =
        runTest(mainDispatcherRule.dispatcher) {
            val combatDomain = CombatTestFactory.validCombatDomain(id = 7L, combatConfigId = 3L)
            whenever(api.startOrResumeCombat(3L)).thenReturn(CombatDataMapper.toDTO(combatDomain))

            viewModel.start(3L)
            advanceUntilIdle()

            val active = viewModel.active.value
            assertEquals(7L, active?.id)
            assertEquals(CombatGeneralStatus.ONGOING, active?.combatStatus)
        }

    @Test
    fun `start with server error sets uiState to GeneralError`() =
        runTest(mainDispatcherRule.dispatcher) {
            doThrow(createFakeHttpException(HTTP_INTERNAL_ERROR))
                .whenever(api)
                .startOrResumeCombat(3L)

            viewModel.start(3L)
            advanceUntilIdle()

            assertNull(viewModel.active.value)
            assertTrue(viewModel.uiState.value is UIState.GeneralError)
        }

    // -------------------------------------------------------------------------
    // executeTurn
    // -------------------------------------------------------------------------

    @Test
    fun `executeTurn with ongoing result updates turn number and last actions`() =
        runTest(mainDispatcherRule.dispatcher) {
            val combatDomain = CombatTestFactory.validCombatDomain(id = 1L, turnNumber = 0)
            val turnResult = CombatTestFactory.validCombatTurnResultOngoing(combatId = 1L, turnNumber = 1)

            whenever(api.startOrResumeCombat(1L)).thenReturn(CombatDataMapper.toDTO(combatDomain))
            whenever(api.executeTurn(1L, 5L)).thenReturn(CombatDataMapper.toDTO(turnResult))

            viewModel.start(1L)
            advanceUntilIdle()
            viewModel.executeTurn(5L)
            advanceUntilIdle()

            assertEquals(1, viewModel.active.value?.turnNumber)
            assertEquals(turnResult.actions, viewModel.lastTurnActions.value)
            assertEquals(CombatGeneralStatus.ONGOING, viewModel.active.value?.combatStatus)
        }

    @Test
    fun `executeTurn with USER_WON keeps active populated for UI`() =
        runTest(mainDispatcherRule.dispatcher) {
            val combatDomain = CombatTestFactory.validCombatDomain(id = 1L)
            val turnResult = CombatTestFactory.userWonTurnResult(combatId = 1L)

            whenever(api.startOrResumeCombat(1L)).thenReturn(CombatDataMapper.toDTO(combatDomain))
            whenever(api.executeTurn(1L, 5L)).thenReturn(CombatDataMapper.toDTO(turnResult))

            viewModel.start(1L)
            advanceUntilIdle()
            viewModel.executeTurn(5L)
            advanceUntilIdle()

            val active = viewModel.active.value
            assertEquals(CombatGeneralStatus.USER_WON, active?.combatStatus)
            assertEquals("COMPLETE_QUEST", active?.onCompletedEvent)
            assertEquals(42, active?.onCompletedEntityId)
        }

    @Test
    fun `executeTurn with no active combat is a no-op`() =
        runTest(mainDispatcherRule.dispatcher) {
            viewModel.executeTurn(5L)
            advanceUntilIdle()

            verify(api, never()).executeTurn(any(), any())
            assertNull(viewModel.active.value)
        }

    // -------------------------------------------------------------------------
    // dismiss
    // -------------------------------------------------------------------------

    @Test
    fun `onVictoryContinue emits onCompletedEvent and clears active`() =
        runTest(mainDispatcherRule.dispatcher) {
            val combatDomain = CombatTestFactory.validCombatDomain(id = 1L)
            val turnResult =
                CombatTestFactory.userWonTurnResult(
                    combatId = 1L,
                    onCompletedEvent = "COMPLETE_QUEST",
                    onCompletedEntityId = 42,
                )

            whenever(api.startOrResumeCombat(1L)).thenReturn(CombatDataMapper.toDTO(combatDomain))
            whenever(api.executeTurn(1L, 5L)).thenReturn(CombatDataMapper.toDTO(turnResult))

            viewModel.start(1L)
            advanceUntilIdle()
            viewModel.executeTurn(5L)
            advanceUntilIdle()

            val emittedEvent =
                async {
                    EventBus.eventFlow.first { (type, _) -> type == EventType.COMPLETE_QUEST }
                }

            viewModel.onVictoryContinue()
            advanceUntilIdle()

            val (type, payload) = emittedEvent.await()
            assertEquals(EventType.COMPLETE_QUEST, type)
            assertEquals(42, (payload as EventPayload.EntityIdPayload).targetEntityId)
            assertNull(viewModel.active.value)
            assertTrue(viewModel.lastTurnActions.value.isEmpty())
        }

    @Test
    fun `dismiss after USER_LOST clears active without emitting onCompletedEvent`() =
        runTest(mainDispatcherRule.dispatcher) {
            val combatDomain = CombatTestFactory.validCombatDomain(id = 1L)
            val turnResult =
                CombatTestFactory.userLostTurnResult(
                    combatId = 1L,
                    onCompletedEvent = "COMPLETE_QUEST",
                    onCompletedEntityId = 99,
                )

            whenever(api.startOrResumeCombat(1L)).thenReturn(CombatDataMapper.toDTO(combatDomain))
            whenever(api.executeTurn(1L, 5L)).thenReturn(CombatDataMapper.toDTO(turnResult))

            viewModel.start(1L)
            advanceUntilIdle()
            viewModel.executeTurn(5L)
            advanceUntilIdle()

            var receivedEvent: Pair<EventType, EventPayload>? = null
            val collectorJob =
                launch {
                    EventBus.eventFlow.collect { receivedEvent = it }
                }

            viewModel.dismiss()
            advanceUntilIdle()
            collectorJob.cancel()

            assertNull(receivedEvent)
            assertNull(viewModel.active.value)
            assertTrue(viewModel.lastTurnActions.value.isEmpty())
        }

    @Test
    fun `onVictoryContinue with no follow-up event clears state without emitting`() =
        runTest(mainDispatcherRule.dispatcher) {
            // Backend signals "no follow-up" via the "_" sentinel; the mapper turns it into null.
            val combatDomain = CombatTestFactory.validCombatDomain(id = 1L)
            val turnResult =
                CombatTestFactory.userWonTurnResult(combatId = 1L, onCompletedEvent = null, onCompletedEntityId = null)

            whenever(api.startOrResumeCombat(1L)).thenReturn(CombatDataMapper.toDTO(combatDomain))
            whenever(api.executeTurn(1L, 5L)).thenReturn(CombatDataMapper.toDTO(turnResult))

            viewModel.start(1L)
            advanceUntilIdle()
            viewModel.executeTurn(5L)
            advanceUntilIdle()

            var receivedEvent: Pair<EventType, EventPayload>? = null
            val collectorJob =
                launch {
                    EventBus.eventFlow.collect { receivedEvent = it }
                }

            viewModel.onVictoryContinue()
            advanceUntilIdle()
            collectorJob.cancel()

            assertNull(receivedEvent)
            assertNull(viewModel.active.value)
            assertTrue(viewModel.lastTurnActions.value.isEmpty())
        }

    // -------------------------------------------------------------------------
    // timeout & abandon
    // -------------------------------------------------------------------------

    @Test
    fun `timeout with terminal result updates active to USER_LOST`() =
        runTest(mainDispatcherRule.dispatcher) {
            val combatDomain = CombatTestFactory.validCombatDomain(id = 1L, endsAt = 999L)
            val turnResult = CombatTestFactory.timeoutTurnResult(combatId = 1L)

            whenever(api.startOrResumeCombat(1L)).thenReturn(CombatDataMapper.toDTO(combatDomain))
            whenever(api.timeoutCombat(1L)).thenReturn(CombatDataMapper.toDTO(turnResult))

            viewModel.start(1L)
            advanceUntilIdle()
            viewModel.timeout()
            advanceUntilIdle()

            val active = viewModel.active.value
            assertEquals(CombatGeneralStatus.USER_LOST, active?.combatStatus)
            assertEquals("COMPLETE_QUEST", active?.onCompletedEvent)
        }

    @Test
    fun `abandon with terminal result updates active to USER_LOST`() =
        runTest(mainDispatcherRule.dispatcher) {
            val combatDomain = CombatTestFactory.validCombatDomain(id = 1L)
            val turnResult = CombatTestFactory.abandonTurnResult(combatId = 1L)

            whenever(api.startOrResumeCombat(1L)).thenReturn(CombatDataMapper.toDTO(combatDomain))
            whenever(api.abandonCombat(1L)).thenReturn(CombatDataMapper.toDTO(turnResult))

            viewModel.start(1L)
            advanceUntilIdle()
            viewModel.abandon()
            advanceUntilIdle()

            val active = viewModel.active.value
            assertEquals(CombatGeneralStatus.USER_LOST, active?.combatStatus)
            assertEquals("COMPLETE_QUEST", active?.onCompletedEvent)
        }
}
