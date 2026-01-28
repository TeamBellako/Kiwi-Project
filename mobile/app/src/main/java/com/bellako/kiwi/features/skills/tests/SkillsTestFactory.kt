package com.bellako.kiwi.features.skills.tests

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillsState
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("MagicNumber")
object SkillsTestFactory {
    @RequiresApi(Build.VERSION_CODES.O)
    fun validSkillsState(): SkillsState =
        SkillsState(
            skills =
                listOf(
                    timeCooldownSkillEquipped(),
                    goalCooldownSkillEquipped(),
                    otherCooldownSkillUnequipped(),
                    skill1(),
                    skill2(),
                ),
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeCooldownSkillEquipped(): SkillDomain =
        SkillDomain.Time(
            id = 1,
            name = "Time Skill",
            description = "Skill with time-based cooldown",
            quote = "Patience is power",
            icon = 1,
            levelupSkillId = null,
            isCooldown = false,
            deckSlot = 1,
            cooldownTimeMinutes = 10,
            cooldownUntil = Instant.now().plus(10, ChronoUnit.MINUTES),
        )

    fun goalCooldownSkillEquipped(): SkillDomain =
        SkillDomain.Goal(
            id = 2,
            name = "Goal Skill",
            description = "Cooldown ends when a goal is completed and mucho mas texto sobre lo que hace",
            quote = "Goal skill quote",
            icon = 2,
            levelupSkillId = null,
            isCooldown = true,
            deckSlot = 2,
            cooldownGoalId = 1001L,
            goalAction = "Do 10 push ups",
            goalProgress = 1,
            goalTarget = 10,
        )

    fun otherCooldownSkillUnequipped(): SkillDomain =
        SkillDomain.Other(
            id = 3,
            name = "Fireball",
            description = "Special cooldown condition",
            quote = null,
            icon = 3,
            levelupSkillId = null,
            isCooldown = false,
            deckSlot = 0,
            cooldownOtherDescription = "After boss defeated",
        )

    fun skill1(): SkillDomain =
        SkillDomain.Time(
            id = 4,
            name = "Frost",
            description =
                "Frost skill with timed cooldown, skill with timed cooldown , skill " +
                    "with timed cooldown ,skill with timed cooldown skill.",
            quote = "Cuando el grajo vuela bajo hace un frio del carajo.",
            icon = 3,
            levelupSkillId = 5L,
            isCooldown = true,
            deckSlot = 4,
            cooldownTimeMinutes = 60,
            cooldownUntil = null,
        )

    fun skill2(): SkillDomain =
        SkillDomain.Other(
            id = 99,
            name = "Smite",
            description = "Just obtained skill",
            quote = null,
            icon = 1,
            levelupSkillId = null,
            isCooldown = false,
            deckSlot = 0,
            cooldownOtherDescription = "",
        )
}
