package com.bellako.kiwi.features.skills.data

import java.time.Instant

sealed class SkillDomain {
    abstract val id: Long
    abstract val name: String
    abstract val description: String
    abstract val quote: String?
    abstract val icon: Int
    abstract val levelupSkillId: Long?
    abstract val isCooldown: Boolean
    abstract val deckSlot: Int

    data class Other(
        override val id: Long,
        override val name: String,
        override val description: String,
        override val quote: String?,
        override val icon: Int,
        override val levelupSkillId: Long?,
        override val isCooldown: Boolean,
        override val deckSlot: Int,
        val cooldownOtherDescription: String,
    ) : SkillDomain()

    data class Time(
        override val id: Long,
        override val name: String,
        override val description: String,
        override val quote: String?,
        override val icon: Int,
        override val levelupSkillId: Long?,
        override val isCooldown: Boolean,
        override val deckSlot: Int,
        val cooldownTimeMinutes: Int,
        val cooldownUntil: Instant?,
    ) : SkillDomain()

    data class Goal(
        override val id: Long,
        override val name: String,
        override val description: String,
        override val quote: String?,
        override val icon: Int,
        override val levelupSkillId: Long?,
        override val isCooldown: Boolean,
        override val deckSlot: Int,
        val cooldownGoalId: Long,
        val goalData: GoalData?,
    ) : SkillDomain()
}

data class GoalData(
    val action: String,
    val progress: Int,
    val target: Int,
)
