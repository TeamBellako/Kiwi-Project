package com.bellako.kiwi.features.combat.data

import kotlinx.serialization.Serializable

@Serializable
enum class CombatGeneralStatus {
    ONGOING,
    USER_WON,
    USER_LOST,
}

@Serializable
enum class CombatActor {
    USER,
    ENEMY,
    ALLY,
}

@Serializable
enum class CombatActionType {
    SKILL_USED,
    ACTOR_BLOCKED_BY_STATE,
    SKILL_REPEAT_BY_STATE,
    ACTOR_DAMAGED_BY_STATE,
    BLOCKED_SKILLS_BY_STATE,
    RELEASED_SKILLS_BY_STATE,
    SKIP,
    STATUS_TURN_REDUCED,
    STATUS_FINISHED,
    TIMEOUT,
    ABANDON,
}

@Serializable
enum class SkillEffectResultType {
    DAMAGE,
    HEAL,
    MODIFY_STAT,
    STATUS_APPLIED,
    STATUS_REMOVED,
    MISS,
}

@Serializable
data class CombatStatsDomain(
    val currentHp: Int,
    val maxHp: Int,
    val patk: Int,
    val matk: Int,
    val pdef: Int,
    val mdef: Int,
    val acc: Int,
    val eva: Int,
    val lck: Int,
)

@Serializable
data class ElementMultiplierDomain(
    val elementId: Long,
    val name: String,
    val icon: Int,
    val description: String,
    val multiplier: Float,
)

@Serializable
data class StatusResistanceDomain(
    val stateId: Long,
    val stateName: String,
    val stateDescription: String,
    val stateIcon: Int,
    val resistance: Float,
)

@Serializable
data class CombatActiveStatusDomain(
    val stateId: Long,
    val name: String,
    val icon: Int? = null,
    val description: String,
    val remainingTurns: Int,
    val value: Float? = null,
)

@Serializable
data class SkillEffectResultDomain(
    val typeResult: SkillEffectResultType,
    val target: CombatActor,
    val statAffected: String? = null,
    val value: Float? = null,
    val critic: Boolean = false,
    val appliedStatus: CombatActiveStatusDomain? = null,
)

@Serializable
data class CombatActionDomain(
    val actor: CombatActor,
    val actionType: CombatActionType,
    val stateName: String? = null,
    val stateId: Long? = null,
    val stateEffectValue: Float? = null,
    val blockedSkills: List<Long>? = null,
    val skillName: String? = null,
    val skillEffectsResults: List<SkillEffectResultDomain> = emptyList(),
)

@Serializable
data class CombatActorDomain(
    val stats: CombatStatsDomain,
    val elementalMultipliers: List<ElementMultiplierDomain> = emptyList(),
    val statusResistances: List<StatusResistanceDomain> = emptyList(),
    val activeStatus: List<CombatActiveStatusDomain> = emptyList(),
)

@Serializable
data class CombatDomain(
    val id: Long,
    val combatConfigId: Long,
    val turnNumber: Int,
    val endsAt: Long? = null,
    val combatStatus: CombatGeneralStatus,
    val enemyName: String,
    val enemySprite: String,
    val background: String? = null,
    val music: String? = null,
    val user: CombatActorDomain,
    val enemy: CombatActorDomain,
    val log: List<CombatActionDomain> = emptyList(),
    val barks: List<CombatBarkTriggerDomain> = emptyList(),
    val firedBarkIds: List<Long> = emptyList(),
    /** Populated only after a terminal turn so dismiss() can fire the follow-up event. */
    val onCompletedEvent: String? = null,
    val onCompletedEntityId: Int? = null,
)

@Serializable
data class CombatTurnResultDomain(
    val combatId: Long,
    val turnNumber: Int,
    val actions: List<CombatActionDomain> = emptyList(),
    val combatStatus: CombatGeneralStatus,
    val onCompletedEvent: String? = null,
    val onCompletedEntityId: Int? = null,
)
