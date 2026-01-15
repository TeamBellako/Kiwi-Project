package com.bellako.kiwi.features.skills.tests

import com.bellako.kiwi.features.skills.data.CooldownType
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillsState
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("MagicNumber")
object SkillsTestFactory {
    fun validSkillsState(): SkillsState =
        SkillsState(
            skills =
                listOf(
                    timeCooldownSkillEquipped(),
                    goalCooldownSkillEquipped(),
                    otherCooldownSkillUnequipped(),
                    noCooldownSkillUnequipped(),
                ),
        )

    fun timeCooldownSkillEquipped(): SkillDomain =
        SkillDomain(
            id = 1,
            name = "Time Cooldown Skill",
            description = "Skill with time-based cooldown",
            quote = "Patience is power",
            icon = 1,
            cooldownType = CooldownType.TIME,
            cooldownGoalId = null,
            cooldownTimeMinutes = 10,
            cooldownOtherDescription = null,
            levelupSkillId = null,
            isCooldown = true,
            cooldownUntil = Instant.now().plus(10, ChronoUnit.MINUTES),
            deckSlot = 1,
        )

    fun goalCooldownSkillEquipped(): SkillDomain =
        SkillDomain(
            id = 2,
            name = "Goal Cooldown Skill",
            description = "Cooldown ends when a goal is completed",
            quote = null,
            icon = 2,
            cooldownType = CooldownType.GOAL,
            cooldownGoalId = 1001L,
            cooldownTimeMinutes = null,
            cooldownOtherDescription = null,
            levelupSkillId = null,
            isCooldown = true,
            cooldownUntil = null,
            deckSlot = 2,
        )

    fun otherCooldownSkillUnequipped(): SkillDomain =
        SkillDomain(
            id = 3,
            name = "Other Cooldown Skill",
            description = "Special cooldown condition",
            quote = null,
            icon = 3,
            cooldownType = CooldownType.OTHER,
            cooldownGoalId = null,
            cooldownTimeMinutes = null,
            cooldownOtherDescription = "After boss defeated",
            levelupSkillId = null,
            isCooldown = true,
            cooldownUntil = null,
            deckSlot = 0,
        )

    fun noCooldownSkillUnequipped(): SkillDomain =
        SkillDomain(
            id = 4,
            name = "Passive Skill",
            description = "No cooldown skill",
            quote = "Always on",
            icon = 4,
            cooldownType = CooldownType.TIME,
            cooldownGoalId = null,
            cooldownTimeMinutes = null,
            cooldownOtherDescription = null,
            levelupSkillId = 5L,
            isCooldown = false,
            cooldownUntil = null,
            deckSlot = 0,
        )

    fun newSkill(): SkillDomain =
        SkillDomain(
            id = 99,
            name = "New Skill",
            description = "Just obtained skill",
            quote = null,
            icon = 99,
            cooldownType = CooldownType.TIME,
            cooldownGoalId = null,
            cooldownTimeMinutes = null,
            cooldownOtherDescription = null,
            levelupSkillId = null,
            isCooldown = false,
            cooldownUntil = null,
            deckSlot = 0,
        )

}
