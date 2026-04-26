package com.bellako.kiwi.features.combat.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.combat.data.CombatTurnResultDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
@Suppress("TooGenericExceptionCaught")
class CombatViewModel
    @Inject
    constructor(
        private val repository: CombatRepository,
    ) : BaseViewModel() {
        private val _active = MutableStateFlow<CombatDomain?>(null)
        val active: StateFlow<CombatDomain?> = _active.asStateFlow()

        private val _lastTurnActions = MutableStateFlow<List<CombatActionDomain>>(emptyList())
        val lastTurnActions: StateFlow<List<CombatActionDomain>> = _lastTurnActions.asStateFlow()

        init {
            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.START_COMBAT) { payload ->
                    val p = payload as EventPayload.EntityIdPayload
                    start(p.targetEntityId.toLong())
                }
            }
        }

        fun start(combatConfigId: Long) {
            viewModelScope.launch {
                try {
                    val combat = repository.startOrResumeCombat(combatConfigId)
                    _active.value = combat
                    _lastTurnActions.value = combat.log
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                }
            }
        }

        fun executeTurn(skillId: Long) {
            val current = _active.value ?: return
            viewModelScope.launch {
                try {
                    val result = repository.executeTurn(current.id, skillId)
                    applyTurnResult(result)
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                }
            }
        }

        fun timeout() {
            val current = _active.value ?: return
            viewModelScope.launch {
                try {
                    val result = repository.timeoutCombat(current.id)
                    applyTurnResult(result)
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                }
            }
        }

        fun abandon() {
            val current = _active.value ?: return
            viewModelScope.launch {
                try {
                    val result = repository.abandonCombat(current.id)
                    applyTurnResult(result)
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                }
            }
        }

        fun dismiss() {
            val current = _active.value ?: return
            viewModelScope.launch {
                val event = current.onCompletedEvent
                val entityId = current.onCompletedEntityId

                val shouldTriggerOnCompletedEvent =
                    current.combatStatus == CombatGeneralStatus.USER_WON &&
                        event != null &&
                        event != "_" &&
                        entityId != null

                if (shouldTriggerOnCompletedEvent) {
                    EventBus.emitEvent(
                        EventType.valueOf(event),
                        EventPayload.EntityIdPayload(entityId),
                    )
                }
                _active.value = null
                _lastTurnActions.value = emptyList()
            }
        }

        private fun applyTurnResult(result: CombatTurnResultDomain) {
            val current = _active.value ?: return
            val isTerminal = result.combatStatus != CombatGeneralStatus.ONGOING
            _active.value =
                current.copy(
                    turnNumber = result.turnNumber,
                    combatStatus = result.combatStatus,
                    log = current.log + result.actions,
                    onCompletedEvent = if (isTerminal) result.onCompletedEvent else null,
                    onCompletedEntityId = if (isTerminal) result.onCompletedEntityId else null,
                )
            _lastTurnActions.value = result.actions
        }
    }
