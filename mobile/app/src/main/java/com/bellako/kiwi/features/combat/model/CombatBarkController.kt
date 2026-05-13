package com.bellako.kiwi.features.combat.model

import com.bellako.kiwi.common.services.PersonalityScriptVariableResolver
import com.bellako.kiwi.common.utils.Logger.info
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.combat.data.ActiveBarkDomain
import com.bellako.kiwi.features.combat.data.BarkDismissMode
import com.bellako.kiwi.features.combat.data.BarkTriggerType
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatActorDomain
import com.bellako.kiwi.features.combat.data.CombatBarkTriggerDomain
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.conversations.model.ConversationsRepository
import com.bellako.kiwi.features.personality.model.IPersonalityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val MS_PER_SECOND = 1000L

@Singleton
@Suppress("TooGenericExceptionCaught")
class CombatBarkController
    @Inject
    constructor(
        private val conversationsRepository: ConversationsRepository,
        private val combatRepository: CombatRepository,
        personalityRepository: IPersonalityRepository,
    ) {
        private val variableResolver = PersonalityScriptVariableResolver(personalityRepository)
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        private val _activeBark = MutableStateFlow<ActiveBarkDomain?>(null)
        val activeBark: StateFlow<ActiveBarkDomain?> = _activeBark.asStateFlow()

        private var combatId: Long = 0L
        private var triggers: List<CombatBarkTriggerDomain> = emptyList()
        private val firedTriggerIds = mutableSetOf<Long>()
        private val pendingQueue = ArrayDeque<CombatBarkTriggerDomain>()
        private val elapsedJobs = mutableListOf<Job>()
        private var fetchJob: Job? = null

        fun onCombatStarted(combat: CombatDomain) {
            cancelTimers()
            combatId = combat.id
            triggers = combat.barks
            firedTriggerIds.clear()
            firedTriggerIds.addAll(combat.firedBarkIds)
            pendingQueue.clear()
            _activeBark.value = null

            info(
                "Bark controller: combat ${combat.id} started with ${triggers.size} trigger(s), " +
                    "${firedTriggerIds.size} already fired",
            )

            // Initial-state triggers: enemy-100% and player-100% (the bark fires the moment combat starts).
            val enemyPct = hpPercent(combat.enemy)
            val playerPct = hpPercent(combat.user)
            triggers
                .asSequence()
                .filter { it.id !in firedTriggerIds }
                .filter { isInitialHpTrigger(it, enemyPct, playerPct) }
                .sortedWith(compareByDescending<CombatBarkTriggerDomain> { it.threshold ?: 0f }.thenByDescending { it.priority })
                .forEach { enqueue(it) }

            scheduleElapsedJobs()
            tryShowNext()
        }

        fun onCombatStateChanged(
            prev: CombatDomain,
            next: CombatDomain,
        ) {
            val matched =
                triggers
                    .asSequence()
                    .filter { it.id !in firedTriggerIds && !pendingQueue.contains(it) }
                    .filter { hpCrossingMatches(it, prev, next) }
                    .sortedWith(compareByDescending<CombatBarkTriggerDomain> { it.threshold ?: 0f }.thenByDescending { it.priority })
                    .toList()
            matched.forEach { enqueue(it) }
            tryShowNext()
        }

        fun onSkillUsed(
            actor: CombatActor,
            skillId: Long,
        ) {
            if (actor != CombatActor.USER) return
            val matched =
                triggers
                    .asSequence()
                    .filter { it.id !in firedTriggerIds && !pendingQueue.contains(it) }
                    .filter { it.type == BarkTriggerType.SKILL_USED && it.skillId == skillId }
                    .sortedByDescending { it.priority }
                    .toList()
            matched.forEach { enqueue(it) }
            tryShowNext()
        }

        fun onCombatEnded() {
            cancelTimers()
            triggers = emptyList()
            firedTriggerIds.clear()
            pendingQueue.clear()
            _activeBark.value = null
            combatId = 0L
        }

        fun dismiss() {
            val current = _activeBark.value ?: return
            firedTriggerIds.add(current.triggerId)
            persistFired(current.triggerId)
            _activeBark.value = null
            tryShowNext()
        }

        suspend fun awaitNoActiveBark() {
            _activeBark.first { it == null }
        }

        private fun isInitialHpTrigger(
            trigger: CombatBarkTriggerDomain,
            enemyPct: Float,
            playerPct: Float,
        ): Boolean {
            val threshold = trigger.threshold ?: return false
            return when (trigger.type) {
                BarkTriggerType.ENEMY_HP_PERCENT -> enemyPct <= threshold
                BarkTriggerType.PLAYER_HP_PERCENT -> playerPct <= threshold
                else -> false
            }
        }

        private fun hpCrossingMatches(
            trigger: CombatBarkTriggerDomain,
            prev: CombatDomain,
            next: CombatDomain,
        ): Boolean {
            val threshold = trigger.threshold ?: return false
            return when (trigger.type) {
                BarkTriggerType.ENEMY_HP_PERCENT ->
                    crossedDownward(hpPercent(prev.enemy), hpPercent(next.enemy), threshold)
                BarkTriggerType.PLAYER_HP_PERCENT ->
                    crossedDownward(hpPercent(prev.user), hpPercent(next.user), threshold)
                else -> false
            }
        }

        private fun crossedDownward(
            prevPct: Float,
            nextPct: Float,
            threshold: Float,
        ): Boolean = prevPct > threshold && nextPct <= threshold

        private fun hpPercent(actor: CombatActorDomain): Float {
            val max = actor.stats.maxHp
            if (max <= 0) return 0f
            return (actor.stats.currentHp.toFloat() / max) * HUNDRED
        }

        private fun scheduleElapsedJobs() {
            triggers
                .filter { it.type == BarkTriggerType.COMBAT_ELAPSED_SECONDS && it.id !in firedTriggerIds }
                .forEach { trigger ->
                    val seconds = trigger.threshold ?: return@forEach
                    val job =
                        scope.launch {
                            delay((seconds * MS_PER_SECOND).toLong())
                            if (trigger.id !in firedTriggerIds) {
                                enqueue(trigger)
                                tryShowNext()
                            }
                        }
                    elapsedJobs += job
                }
        }

        private fun cancelTimers() {
            elapsedJobs.forEach { it.cancel() }
            elapsedJobs.clear()
            fetchJob?.cancel()
            fetchJob = null
        }

        private fun enqueue(trigger: CombatBarkTriggerDomain) {
            if (pendingQueue.size >= MAX_QUEUE) return
            pendingQueue.addLast(trigger)
        }

        private fun tryShowNext() {
            if (_activeBark.value != null) return
            val trigger = pendingQueue.removeFirstOrNull() ?: return
            fetchJob =
                scope.launch {
                    val active = fetchActive(trigger) ?: return@launch
                    if (_activeBark.value == null) {
                        _activeBark.value = active
                    } else {
                        pendingQueue.addFirst(trigger)
                    }
                }
        }

        private suspend fun fetchActive(trigger: CombatBarkTriggerDomain): ActiveBarkDomain? =
            try {
                val raw = conversationsRepository.getById(trigger.conversationId)
                ActiveBarkDomain(
                    triggerId = trigger.id,
                    conversation = raw.copy(dialog = raw.readDialog(variableResolver)),
                    dismissMode = trigger.dismissMode,
                )
            } catch (e: Throwable) {
                warn("Failed to load bark conversation ${trigger.conversationId}: ${e.message}")
                null
            }

        private fun persistFired(triggerId: Long) {
            val id = combatId
            if (id == 0L) return
            scope.launch {
                try {
                    combatRepository.markBarkFired(id, triggerId)
                } catch (e: Throwable) {
                    warn("Failed to persist fired bark $triggerId: ${e.message}")
                }
            }
        }

        companion object {
            private const val HUNDRED = 100f
            private const val MAX_QUEUE = 3
        }
    }
