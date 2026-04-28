package com.bellako.kiwi.features.combat.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatActiveStatusDomain
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatActorDomain
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.combat.data.CombatTurnResultDomain
import com.bellako.kiwi.features.combat.data.SkillEffectResultDomain
import com.bellako.kiwi.features.combat.data.SkillEffectResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DISMISS_ANIMATION_DURATION_MS = 500L
private const val TURN_INITIAL_DELAY_MS = 250L
private const val TURN_BEAT_MS = 1500L

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

        private val _isVisible = MutableStateFlow(false)
        val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

        private val _lastTurnActions = MutableStateFlow<List<CombatActionDomain>>(emptyList())
        val lastTurnActions: StateFlow<List<CombatActionDomain>> = _lastTurnActions.asStateFlow()

        private val _isTurnPlaying = MutableStateFlow(false)
        val isTurnPlaying: StateFlow<Boolean> = _isTurnPlaying.asStateFlow()

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
                    _isVisible.value = true
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                }
            }
        }

        fun tryResumeActive() {
            if (_active.value != null) return
            viewModelScope.launch {
                try {
                    val combat = repository.getActiveCombat() ?: return@launch
                    if (combat.combatStatus != CombatGeneralStatus.ONGOING) return@launch
                    _active.value = combat
                    _lastTurnActions.value = combat.log
                    _isVisible.value = true
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                }
            }
        }

        fun confirmAbandon() {
            val current = _active.value ?: return
            viewModelScope.launch {
                try {
                    repository.abandonCombat(current.id)
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                }
                dismiss()
            }
        }

        fun executeTurn(
            skillId: Long,
            skillName: String? = null,
        ) {
            val current = _active.value ?: return
            if (_isTurnPlaying.value) return

            val hasOptimisticPlayerSkill = !skillName.isNullOrBlank()
            if (hasOptimisticPlayerSkill) {
                val optimisticAction =
                    CombatActionDomain(
                        actor = CombatActor.USER,
                        actionType = CombatActionType.SKILL_USED,
                        skillName = skillName,
                    )
                _active.value = current.copy(log = current.log + optimisticAction)
            }

            viewModelScope.launch {
                _isTurnPlaying.value = true
                try {
                    val result = repository.executeTurn(current.id, skillId)
                    EventBus.emitEvent(
                        EventType.THROW_SKILL,
                        EventPayload.EntityIdPayload(skillId.toInt()),
                    )
                    playTurnResult(result, hasOptimisticPlayerSkill = hasOptimisticPlayerSkill)
                } catch (e: Throwable) {
                    if (hasOptimisticPlayerSkill) {
                        _active.value = current
                    }
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    _isTurnPlaying.value = false
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

                _isVisible.value = false
                delay(DISMISS_ANIMATION_DURATION_MS)

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

        private suspend fun playTurnResult(
            result: CombatTurnResultDomain,
            hasOptimisticPlayerSkill: Boolean = false,
        ) {
            val initial = _active.value ?: return
            var state = initial.copy(turnNumber = result.turnNumber)
            _active.value = state

            val actions = result.actions
            var startIndex = 0

            if (hasOptimisticPlayerSkill &&
                actions.firstOrNull()?.actor == CombatActor.USER &&
                actions.first().actionType == CombatActionType.SKILL_USED
            ) {
                state = applyActionEffects(state, actions.first())
                _active.value = state
                delay(TURN_BEAT_MS)
                startIndex = 1
            } else if (actions.isNotEmpty()) {
                delay(TURN_INITIAL_DELAY_MS)
            }

            for (i in startIndex until actions.size) {
                val action = actions[i]
                state = state.copy(log = state.log + action)
                state = applyActionEffects(state, action)
                _active.value = state
                delay(TURN_BEAT_MS)
            }

            val isTerminal = result.combatStatus != CombatGeneralStatus.ONGOING
            _active.value =
                state.copy(
                    combatStatus = result.combatStatus,
                    onCompletedEvent = if (isTerminal) result.onCompletedEvent else null,
                    onCompletedEntityId = if (isTerminal) result.onCompletedEntityId else null,
                )
            _lastTurnActions.value = result.actions
        }

        private fun applyActionEffects(
            combat: CombatDomain,
            action: CombatActionDomain,
        ): CombatDomain =
            when (action.actionType) {
                CombatActionType.SKILL_USED ->
                    action.skillEffectsResults.fold(combat) { acc, effect -> applyEffect(acc, effect) }
                CombatActionType.ACTOR_DAMAGED_BY_STATE -> {
                    val damage = (action.stateEffectValue ?: 0f).toInt()
                    if (damage == 0) combat else applyHpDelta(combat, action.actor, -damage)
                }
                CombatActionType.STATUS_TURN_REDUCED ->
                    updateActiveStatuses(combat, action.actor) { statuses ->
                        statuses.map { status ->
                            if (status.stateId == action.stateId) {
                                status.copy(remainingTurns = status.remainingTurns - 1)
                            } else {
                                status
                            }
                        }
                    }
                CombatActionType.STATUS_FINISHED ->
                    updateActiveStatuses(combat, action.actor) { statuses ->
                        statuses.filterNot { it.stateId == action.stateId }
                    }
                else -> combat
            }

        private fun applyEffect(
            combat: CombatDomain,
            effect: SkillEffectResultDomain,
        ): CombatDomain =
            when (effect.typeResult) {
                SkillEffectResultType.DAMAGE ->
                    applyHpDelta(combat, effect.target, -(effect.value ?: 0f).toInt())
                SkillEffectResultType.HEAL ->
                    applyHpDelta(combat, effect.target, (effect.value ?: 0f).toInt())
                SkillEffectResultType.MODIFY_STAT ->
                    applyStatDelta(combat, effect.target, effect.statAffected, (effect.value ?: 0f).toInt())
                SkillEffectResultType.STATUS_APPLIED -> {
                    val status = effect.appliedStatus
                    if (status == null) {
                        combat
                    } else {
                        updateActiveStatuses(combat, effect.target) { it + status }
                    }
                }
                SkillEffectResultType.STATUS_REMOVED -> {
                    val statusId = effect.appliedStatus?.stateId
                    if (statusId == null) {
                        combat
                    } else {
                        updateActiveStatuses(combat, effect.target) { statuses ->
                            statuses.filterNot { it.stateId == statusId }
                        }
                    }
                }
                SkillEffectResultType.MISS -> combat
            }

        private fun applyHpDelta(
            combat: CombatDomain,
            target: CombatActor,
            delta: Int,
        ): CombatDomain =
            updateActor(combat, target) { actor ->
                val newHp = (actor.stats.currentHp + delta).coerceIn(0, actor.stats.maxHp)
                actor.copy(stats = actor.stats.copy(currentHp = newHp))
            }

        @Suppress("CyclomaticComplexMethod")
        private fun applyStatDelta(
            combat: CombatDomain,
            target: CombatActor,
            statName: String?,
            delta: Int,
        ): CombatDomain {
            if (statName == null) return combat
            return updateActor(combat, target) { actor ->
                val s = actor.stats
                val newStats =
                    when (statName.lowercase()) {
                        "currenthp" ->
                            s.copy(currentHp = (s.currentHp + delta).coerceIn(0, s.maxHp))
                        "maxhp" -> s.copy(maxHp = s.maxHp + delta)
                        "patk" -> s.copy(patk = s.patk + delta)
                        "matk" -> s.copy(matk = s.matk + delta)
                        "pdef" -> s.copy(pdef = s.pdef + delta)
                        "mdef" -> s.copy(mdef = s.mdef + delta)
                        "acc" -> s.copy(acc = s.acc + delta)
                        "eva" -> s.copy(eva = s.eva + delta)
                        "lck" -> s.copy(lck = s.lck + delta)
                        else -> s
                    }
                actor.copy(stats = newStats)
            }
        }

        private fun updateActiveStatuses(
            combat: CombatDomain,
            target: CombatActor,
            transform: (List<CombatActiveStatusDomain>) -> List<CombatActiveStatusDomain>,
        ): CombatDomain = updateActor(combat, target) { it.copy(activeStatus = transform(it.activeStatus)) }

        private fun updateActor(
            combat: CombatDomain,
            target: CombatActor,
            transform: (CombatActorDomain) -> CombatActorDomain,
        ): CombatDomain =
            when (target) {
                CombatActor.USER -> combat.copy(user = transform(combat.user))
                CombatActor.ENEMY -> combat.copy(enemy = transform(combat.enemy))
                CombatActor.ALLY -> combat
            }
    }
