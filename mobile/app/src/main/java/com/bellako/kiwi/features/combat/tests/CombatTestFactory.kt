package com.bellako.kiwi.features.combat.tests

import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatActorDomain
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.combat.data.CombatStatsDomain
import com.bellako.kiwi.features.combat.data.CombatTurnResultDomain
import com.bellako.kiwi.features.combat.data.SkillEffectResultDomain
import com.bellako.kiwi.features.combat.data.SkillEffectResultType

@Suppress("MagicNumber", "LongParameterList", "TooManyFunctions")
object CombatTestFactory {
    fun validCombatStatsDomain(
        currentHp: Int = 100,
        maxHp: Int = 100,
        patk: Int = 20,
        matk: Int = 20,
        pdef: Int = 10,
        mdef: Int = 10,
        acc: Int = 90,
        eva: Int = 10,
        lck: Int = 5,
    ): CombatStatsDomain =
        CombatStatsDomain(
            currentHp = currentHp,
            maxHp = maxHp,
            patk = patk,
            matk = matk,
            pdef = pdef,
            mdef = mdef,
            acc = acc,
            eva = eva,
            lck = lck,
        )

    fun validCombatActorDomain(stats: CombatStatsDomain = validCombatStatsDomain()): CombatActorDomain =
        CombatActorDomain(
            stats = stats,
            elementalMultipliers = emptyList(),
            statusResistances = emptyList(),
            activeStatus = emptyList(),
        )

    fun validCombatDomain(
        id: Long = 1L,
        combatConfigId: Long = 1L,
        turnNumber: Int = 0,
        endsAt: Long? = null,
        combatStatus: CombatGeneralStatus = CombatGeneralStatus.ONGOING,
        enemyName: String = "Test Enemy",
        enemySprite: String = "enemy_sprite",
        backgroundId: Long? = null,
        user: CombatActorDomain = validCombatActorDomain(),
        enemy: CombatActorDomain = validCombatActorDomain(),
        log: List<CombatActionDomain> = emptyList(),
        onCompletedEvent: String? = null,
        onCompletedEntityId: Int? = null,
    ): CombatDomain =
        CombatDomain(
            id = id,
            combatConfigId = combatConfigId,
            turnNumber = turnNumber,
            endsAt = endsAt,
            combatStatus = combatStatus,
            enemyName = enemyName,
            enemySprite = enemySprite,
            backgroundId = backgroundId,
            user = user,
            enemy = enemy,
            log = log,
            onCompletedEvent = onCompletedEvent,
            onCompletedEntityId = onCompletedEntityId,
        )

    fun skillUsedAction(
        actor: CombatActor = CombatActor.USER,
        skillName: String = "Fireball",
        damage: Float = 25f,
    ): CombatActionDomain =
        CombatActionDomain(
            actor = actor,
            actionType = CombatActionType.SKILL_USED,
            skillName = skillName,
            skillEffectsResults =
                listOf(
                    SkillEffectResultDomain(
                        typeResult = SkillEffectResultType.DAMAGE,
                        target = if (actor == CombatActor.USER) CombatActor.ENEMY else CombatActor.USER,
                        value = damage,
                        critic = false,
                    ),
                ),
        )

    fun timeoutAction(): CombatActionDomain =
        CombatActionDomain(
            actor = CombatActor.USER,
            actionType = CombatActionType.TIMEOUT,
        )

    fun abandonAction(): CombatActionDomain =
        CombatActionDomain(
            actor = CombatActor.USER,
            actionType = CombatActionType.ABANDON,
        )

    fun validCombatTurnResultOngoing(
        combatId: Long = 1L,
        turnNumber: Int = 1,
        actions: List<CombatActionDomain> = listOf(skillUsedAction()),
    ): CombatTurnResultDomain =
        CombatTurnResultDomain(
            combatId = combatId,
            turnNumber = turnNumber,
            actions = actions,
            combatStatus = CombatGeneralStatus.ONGOING,
            onCompletedEvent = null,
            onCompletedEntityId = null,
        )

    fun userWonTurnResult(
        combatId: Long = 1L,
        turnNumber: Int = 5,
        onCompletedEvent: String? = "COMPLETE_QUEST",
        onCompletedEntityId: Int? = 42,
        actions: List<CombatActionDomain> = listOf(skillUsedAction(damage = 100f)),
    ): CombatTurnResultDomain =
        CombatTurnResultDomain(
            combatId = combatId,
            turnNumber = turnNumber,
            actions = actions,
            combatStatus = CombatGeneralStatus.USER_WON,
            onCompletedEvent = onCompletedEvent,
            onCompletedEntityId = onCompletedEntityId,
        )

    fun userLostTurnResult(
        combatId: Long = 1L,
        turnNumber: Int = 5,
        onCompletedEvent: String? = "COMPLETE_QUEST",
        onCompletedEntityId: Int? = 42,
        actions: List<CombatActionDomain> = listOf(skillUsedAction(actor = CombatActor.ENEMY, damage = 200f)),
    ): CombatTurnResultDomain =
        CombatTurnResultDomain(
            combatId = combatId,
            turnNumber = turnNumber,
            actions = actions,
            combatStatus = CombatGeneralStatus.USER_LOST,
            onCompletedEvent = onCompletedEvent,
            onCompletedEntityId = onCompletedEntityId,
        )

    fun timeoutTurnResult(
        combatId: Long = 1L,
        turnNumber: Int = 3,
        onCompletedEvent: String? = "COMPLETE_QUEST",
        onCompletedEntityId: Int? = 42,
    ): CombatTurnResultDomain =
        CombatTurnResultDomain(
            combatId = combatId,
            turnNumber = turnNumber,
            actions = listOf(timeoutAction()),
            combatStatus = CombatGeneralStatus.USER_LOST,
            onCompletedEvent = onCompletedEvent,
            onCompletedEntityId = onCompletedEntityId,
        )

    fun abandonTurnResult(
        combatId: Long = 1L,
        turnNumber: Int = 3,
        onCompletedEvent: String? = "COMPLETE_QUEST",
        onCompletedEntityId: Int? = 42,
    ): CombatTurnResultDomain =
        CombatTurnResultDomain(
            combatId = combatId,
            turnNumber = turnNumber,
            actions = listOf(abandonAction()),
            combatStatus = CombatGeneralStatus.USER_LOST,
            onCompletedEvent = onCompletedEvent,
            onCompletedEntityId = onCompletedEntityId,
        )
}
