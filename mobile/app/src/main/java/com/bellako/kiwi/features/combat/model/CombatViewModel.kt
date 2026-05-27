package com.bellako.kiwi.features.combat.model

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.features.combat.data.ActiveBarkDomain
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DISMISS_ANIMATION_DURATION_MS = 500L
private const val TURN_INITIAL_DELAY_MS = 250L
private const val TURN_BEAT_MS = 1500L

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
@Suppress("TooGenericExceptionCaught", "TooManyFunctions")
class CombatViewModel
    @Inject
    constructor(
        private val repository: CombatRepository,
        private val barkController: CombatBarkController,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel() {
        private val _active = MutableStateFlow<CombatDomain?>(null)
        val active: StateFlow<CombatDomain?> = _active.asStateFlow()

        val activeBark: StateFlow<ActiveBarkDomain?> = barkController.activeBark

        fun dismissBark() = barkController.dismiss()

        private val _isVisible = MutableStateFlow(false)
        val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

        private val _lastTurnActions = MutableStateFlow<List<CombatActionDomain>>(emptyList())
        val lastTurnActions: StateFlow<List<CombatActionDomain>> = _lastTurnActions.asStateFlow()

        private val _isTurnPlaying = MutableStateFlow(false)
        val isTurnPlaying: StateFlow<Boolean> = _isTurnPlaying.asStateFlow()

        // Flips true once tryResumeActive has finished its first run — whether
        // or not it found a combat. MainScreen reads this to (a) keep the
        // initial loading curtain up until the resume question is answered,
        // and (b) suppress map music during that window, so a cold-start
        // resume goes straight to combat music with no map-music flash.
        private val _hasResolvedCombatOnStartup = MutableStateFlow(false)
        val hasResolvedCombatOnStartup: StateFlow<Boolean> = _hasResolvedCombatOnStartup.asStateFlow()

        init {
            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.START_COMBAT) { payload ->
                    val p = payload as EventPayload.EntityIdPayload
                    start(p.targetEntityId.toLong())
                }
            }
            watchForTimeout()
        }

        private fun watchForTimeout() {
            viewModelScope.launch {
                combine(_active, _isTurnPlaying) { combat, isPlaying -> combat to isPlaying }
                    .collectLatest { (combat, isPlaying) ->
                        if (combat == null) return@collectLatest
                        if (combat.combatStatus != CombatGeneralStatus.ONGOING) return@collectLatest
                        if (isPlaying) return@collectLatest
                        val endsAt = combat.endsAt ?: return@collectLatest
                        val remaining = endsAt - System.currentTimeMillis()
                        if (remaining > 0) delay(remaining)
                        timeout()
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
                    barkController.onCombatStarted(combat)
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                }
            }
        }

        fun tryResumeActive() {
            if (_active.value != null) {
                _hasResolvedCombatOnStartup.value = true
                return
            }
            viewModelScope.launch {
                try {
                    val combat = repository.getActiveCombat() ?: return@launch
                    if (combat.combatStatus != CombatGeneralStatus.ONGOING) return@launch
                    _active.value = combat
                    _lastTurnActions.value = combat.log
                    _isVisible.value = true
                    barkController.onCombatStarted(combat)
                } catch (e: Throwable) {
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    // Flip AFTER _isVisible so any observer that reacts to the
                    // flag (curtain wait, map-music gate) sees the combat
                    // already in place when it unblocks.
                    _hasResolvedCombatOnStartup.value = true
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
                    playTurnResult(
                        result,
                        hasOptimisticPlayerSkill = hasOptimisticPlayerSkill,
                        playerSkillId = skillId,
                    )
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
            if (_active.value == null) return
            viewModelScope.launch {
                _isVisible.value = false
                delay(DISMISS_ANIMATION_DURATION_MS)
                _active.value = null
                _lastTurnActions.value = emptyList()
                barkController.onCombatEnded()
            }
        }

        fun onVictoryContinue() {
            val current = _active.value ?: return
            viewModelScope.launch {
                val event = current.onCompletedEvent
                val entityId = current.onCompletedEntityId

                _isVisible.value = false
                delay(DISMISS_ANIMATION_DURATION_MS)

                if (event != null && entityId != null) {
                    EventBus.emitEvent(
                        EventType.valueOf(event),
                        EventPayload.EntityIdPayload(entityId),
                    )
                }
                _active.value = null
                _lastTurnActions.value = emptyList()
                barkController.onCombatEnded()
            }
        }

        private fun applyTurnResult(result: CombatTurnResultDomain) {
            val current = _active.value ?: return
            val isTerminal = result.combatStatus != CombatGeneralStatus.ONGOING
            _active.value =
                current.copy(
                    turnNumber = result.turnNumber,
                    combatStatus = result.combatStatus,
                    log = current.log + result.actions.map { it.copy(createdAt = result.createdAt) },
                    onCompletedEvent = if (isTerminal) result.onCompletedEvent else null,
                    onCompletedEntityId = if (isTerminal) result.onCompletedEntityId else null,
                )
            _lastTurnActions.value = result.actions
        }

        private suspend fun playTurnResult(
            result: CombatTurnResultDomain,
            hasOptimisticPlayerSkill: Boolean = false,
            playerSkillId: Long? = null,
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
                val prev = state
                state = applyActionEffects(state, actions.first())
                // Stamp the optimistically-added player action (the last log
                // entry) with the turn timestamp so it groups with the rest of
                // the turn instead of opening a separate timestamp section.
                state =
                    state.copy(
                        log = state.log.dropLast(1) + state.log.last().copy(createdAt = result.createdAt),
                    )
                _active.value = state
                playActionSFX(actions.first())
                playerSkillId?.let { barkController.onSkillUsed(CombatActor.USER, it) }
                barkController.onCombatStateChanged(prev, state)
                delay(TURN_BEAT_MS)
                barkController.awaitNoActiveBark()
                startIndex = 1
            } else if (actions.isNotEmpty()) {
                delay(TURN_INITIAL_DELAY_MS)
            }

            for (i in startIndex until actions.size) {
                val action = actions[i].copy(createdAt = result.createdAt)
                val prev = state
                state = state.copy(log = state.log + action)
                state = applyActionEffects(state, action)
                _active.value = state
                playActionSFX(action)
                if (action.actor == CombatActor.USER &&
                    action.actionType == CombatActionType.SKILL_USED &&
                    playerSkillId != null
                ) {
                    barkController.onSkillUsed(CombatActor.USER, playerSkillId)
                }
                barkController.onCombatStateChanged(prev, state)
                delay(TURN_BEAT_MS)
                barkController.awaitNoActiveBark()
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
                        "turns" -> s.copy(turns = s.turns + delta)
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

        private fun playActionSFX(action: CombatActionDomain) {
            when (action.actionType) {
                CombatActionType.SKILL_USED ->
                    action.skillEffectsResults.forEach { effect ->
                        resolveEffectSFX(effect)?.let { AudioManager.playSFX(context, it) }
                    }
                CombatActionType.ACTOR_DAMAGED_BY_STATE ->
                    AudioManager.playSFX(context, R.raw.snd_stats_healthdown)
                else -> Unit
            }
        }

        private fun resolveEffectSFX(effect: SkillEffectResultDomain): Int? =
            when (effect.typeResult) {
                SkillEffectResultType.DAMAGE -> R.raw.snd_stats_healthdown
                SkillEffectResultType.HEAL -> R.raw.snd_stats_healthup
                SkillEffectResultType.MODIFY_STAT -> resolveStatSFX(effect.statAffected, effect.value)
                SkillEffectResultType.STATUS_APPLIED -> resolveStatusSFX(effect.appliedStatus, effect.value)
                SkillEffectResultType.STATUS_REMOVED -> R.raw.snd_states_reversion
                SkillEffectResultType.MISS -> null
            }

        private fun resolveStatSFX(
            statAffected: String?,
            value: Float?,
        ): Int {
            val isHp = statAffected?.equals("currentHp", ignoreCase = true) == true
            val positive = (value ?: 0f) >= 0f
            return when {
                isHp && positive -> R.raw.snd_stats_healthup
                isHp -> R.raw.snd_stats_healthdown
                positive -> R.raw.snd_stats_generalstatup
                else -> R.raw.snd_stats_generalstatdown
            }
        }

        @Suppress("CyclomaticComplexMethod")
        private fun resolveStatusSFX(
            status: CombatActiveStatusDomain?,
            value: Float?,
        ): Int {
            val name = status?.name?.lowercase() ?: return R.raw.snd_states_statup
            return when {
                name.contains("freez") || name.contains("froz") -> R.raw.snd_states_freeze
                name.contains("confus") -> R.raw.snd_states_confusion
                name.contains("block") || name.contains("silenc") -> R.raw.snd_states_buffblock
                name.contains("revers") || name.contains("reflect") -> R.raw.snd_states_reversion
                name.contains("burn") || name.contains("poison") || name.contains("venom") ->
                    R.raw.snd_states_burnpoison
                name.contains("mut") -> R.raw.snd_states_mutis
                (value ?: 0f) < 0f -> R.raw.snd_states_statdown
                else -> R.raw.snd_states_statup
            }
        }
    }
