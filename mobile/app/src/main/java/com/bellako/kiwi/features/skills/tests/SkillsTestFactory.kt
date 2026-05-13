package com.bellako.kiwi.features.skills.tests

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.R
import com.bellako.kiwi.features.skills.data.GoalData
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillType
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
                ).associateBy { it.id },
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeCooldownSkillEquipped(): SkillDomain =
        SkillDomain.Time(
            id = 1,
            name = "Strategic Advantage",
            type = SkillType.ADAPTABILITY,
            description = "Skill with time-based cooldown",
            quote = "Patience is power",
            icon = R.drawable.ic_skills_resilience,
            onThrowSFX = R.raw.snd_fx_skill_resilience,
            levelupSkillId = null,
            isCooldown = false,
            deckSlot = 1,
            cooldownTimeMinutes = 10,
            cooldownUntil = Instant.now().plus(10, ChronoUnit.MINUTES),
            0f,
        )

    fun goalCooldownSkillEquipped(): SkillDomain =
        SkillDomain.Goal(
            id = 2,
            name = "Goal Skill",
            type = SkillType.ADAPTABILITY,
            description = "Cooldown ends when a goal is completed and mucho mas texto sobre lo que hace",
            quote = "Goal skill quote",
            icon = R.drawable.ic_skills_resilience,
            onThrowSFX = R.raw.snd_fx_skill_resilience,
            levelupSkillId = null,
            isCooldown = true,
            deckSlot = 2,
            cooldownGoalId = 1001L,
            goalData = GoalData(1L, "Do 10 push ups", 1, 10, 1001L),
        )

    fun otherCooldownSkillUnequipped(): SkillDomain =
        SkillDomain.Other(
            id = 3,
            name = "Fireball",
            type = SkillType.ADAPTABILITY,
            description = "Special cooldown condition",
            quote = null,
            icon = R.drawable.ic_skills_resilience,
            onThrowSFX = R.raw.snd_fx_skill_resilience,
            levelupSkillId = null,
            isCooldown = false,
            deckSlot = 0,
            cooldownOtherDescription = "After boss defeated",
        )

    fun skill1(): SkillDomain =
        SkillDomain.Time(
            id = 4,
            name = "Frost",
            type = SkillType.ADAPTABILITY,
            description =
                "Frost skill with timed cooldown, skill with timed cooldown , skill " +
                    "with timed cooldown ,skill with timed cooldown skill.",
            quote = "Cuando el grajo vuela bajo hace un frio del carajo.",
            icon = R.drawable.ic_skills_resilience,
            onThrowSFX = R.raw.snd_fx_skill_resilience,
            levelupSkillId = 5L,
            isCooldown = true,
            deckSlot = 4,
            cooldownTimeMinutes = 60,
            cooldownUntil = null,
            0f,
        )

    fun skill2(): SkillDomain =
        SkillDomain.Other(
            id = 99,
            name = "Smite",
            type = SkillType.ADAPTABILITY,
            description = "Just obtained skill",
            quote = null,
            icon = R.drawable.ic_skills_resilience,
            onThrowSFX = R.raw.snd_fx_skill_resilience,
            levelupSkillId = null,
            isCooldown = false,
            deckSlot = 0,
            cooldownOtherDescription = "",
        )
}
