package com.bellako.kiwi.features.skills.data

data class SkillDTO(
    val skillId: Long,
    val name: String,
    val type: String,
    val description: String,
    val quote: String?,
    val cooldownType: String,
    val cooldownGoalId: Long?,
    val cooldownTimeMinutes: Int?,
    val cooldownOtherDescription: String?,
    val levelupSkillId: Long?,
    val cooldown: Boolean,
    val cooldownUntil: Long?,
    val deckSlot: Int,
)

enum class CooldownType {
    TIME,
    OTHER,
    GOAL,
}

enum class SkillType {
    ADAPTABILITY,
    CONTROL,
    EMPATHY,
    FOCUS,
    MOTIVATION,
    RESILIENCE,
}
