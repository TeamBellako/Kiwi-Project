package com.bellako.kiwi.features.combat.data

import kotlinx.serialization.Serializable

@Serializable
data class CombatStatsDTO(
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
data class ElementMultiplierDTO(
    val elementId: Long,
    val name: String,
    val icon: Int,
    val description: String,
    val multiplier: Float,
)

@Serializable
data class StatusResistanceDTO(
    val stateId: Long,
    val stateName: String,
    val stateDescription: String,
    val stateIcon: Int,
    val resistance: Float,
)

@Serializable
data class CombatActiveStatusDTO(
    val stateId: Long,
    val name: String,
    val icon: Int? = null,
    val description: String,
    val remainingTurns: Int,
    val value: Float? = null,
)

@Serializable
data class SkillEffectResultDTO(
    val typeResult: String,
    val target: String,
    val statAffected: String? = null,
    val value: Float? = null,
    val critic: Boolean = false,
    val appliedStatus: CombatActiveStatusDTO? = null,
)

@Serializable
data class CombatActionDTO(
    val actor: String,
    val actionType: String,
    val stateName: String? = null,
    val stateId: Long? = null,
    val stateEffectValue: Float? = null,
    val blockedSkills: List<Long>? = null,
    val skillName: String? = null,
    val skillEffectsResults: List<SkillEffectResultDTO> = emptyList(),
)

@Serializable
data class CombatActorDTO(
    val stats: CombatStatsDTO,
    val elementalMultipliers: List<ElementMultiplierDTO> = emptyList(),
    val statusResistances: List<StatusResistanceDTO> = emptyList(),
    val activeStatus: List<CombatActiveStatusDTO> = emptyList(),
)

@Serializable
data class CombatBarkTriggerDTO(
    val id: Long,
    val type: String,
    val threshold: Float? = null,
    val skillId: Long? = null,
    val conversationId: Long,
    val dismissMode: String? = null,
    val priority: Int? = null,
)

@Serializable
data class CombatDTO(
    val id: Long,
    val combatConfigId: Long,
    val turnNumber: Int,
    val endsAt: Long? = null,
    val combatStatus: String,
    val enemyName: String,
    val enemySprite: String,
    val background: String? = null,
    val music: String? = null,
    val user: CombatActorDTO,
    val enemy: CombatActorDTO,
    val log: List<CombatActionDTO> = emptyList(),
    val barks: List<CombatBarkTriggerDTO>? = null,
    val firedBarkIds: List<Long>? = null,
)

@Serializable
data class CombatTurnResultDTO(
    val combatId: Long,
    val turnNumber: Int,
    val actions: List<CombatActionDTO> = emptyList(),
    val combatStatus: String,
    val onCompletedEvent: String,
    val onCompletedEntityId: Int,
)
