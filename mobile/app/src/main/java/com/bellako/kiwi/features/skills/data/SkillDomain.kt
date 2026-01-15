package com.bellako.kiwi.features.skills.data

import java.time.Instant

data class SkillDomain(
    val id: Long,
    val name: String,
    val description: String,
    val quote: String?,
    val icon: Int,
    val cooldownType: CooldownType,
    val cooldownGoalId: Long?,
    val cooldownTimeMinutes: Int?,
    val cooldownOtherDescription: String?,
    val levelupSkillId: Long?,
    val isCooldown: Boolean,
    val cooldownUntil: Instant?,
    val deckSlot: Int,
)

enum class CooldownType {
    TIME,
    GOAL,
    OTHER,
}
