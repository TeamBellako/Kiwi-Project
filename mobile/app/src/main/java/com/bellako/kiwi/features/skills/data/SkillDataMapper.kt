package com.bellako.kiwi.features.skills.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.R
import java.time.Instant

object SkillDataMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomainWithoutGoal(dto: SkillDTO): SkillDomain =
        when (CooldownType.valueOf(dto.cooldownType)) {
            CooldownType.OTHER ->
                SkillDomain.Other(
                    id = dto.skillId,
                    name = dto.name,
                    type = parseSkillType(dto.elementName),
                    description = dto.description,
                    quote = dto.quote,
                    icon = resolveIcon(parseSkillType(dto.elementName)),
                    onThrowSFX = resolveOnThrowSFX(parseSkillType(dto.elementName)),
                    levelupSkillId = dto.levelupSkillId,
                    isCooldown = dto.cooldown,
                    deckSlot = dto.deckSlot,
                    cooldownOtherDescription = dto.cooldownOtherDescription ?: "",
                )

            CooldownType.TIME ->
                SkillDomain.Time(
                    id = dto.skillId,
                    name = dto.name,
                    type = parseSkillType(dto.elementName),
                    description = dto.description,
                    quote = dto.quote,
                    icon = resolveIcon(parseSkillType(dto.elementName)),
                    onThrowSFX = resolveOnThrowSFX(parseSkillType(dto.elementName)),
                    levelupSkillId = dto.levelupSkillId,
                    isCooldown = dto.cooldown,
                    deckSlot = dto.deckSlot,
                    cooldownTimeMinutes =
                        requireNotNull(dto.cooldownTimeMinutes) {
                            "cooldownTimeMinutes is required for TIME cooldown"
                        },
                    cooldownUntil = dto.cooldownUntil?.let { Instant.ofEpochMilli(it) },
                    cooldownProgress = 0f,
                )

            CooldownType.GOAL ->
                error("GOAL skills must be mapped with goal data")
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toGoalDomain(
        dto: SkillDTO,
        goalData: GoalData,
    ): SkillDomain.Goal {
        require(CooldownType.valueOf(dto.cooldownType) == CooldownType.GOAL)

        return SkillDomain.Goal(
            id = dto.skillId,
            name = dto.name,
            type = parseSkillType(dto.elementName),
            description = dto.description,
            quote = dto.quote,
            icon = resolveIcon(parseSkillType(dto.elementName)),
            onThrowSFX = resolveOnThrowSFX(parseSkillType(dto.elementName)),
            levelupSkillId = dto.levelupSkillId,
            isCooldown = dto.cooldown,
            deckSlot = dto.deckSlot,
            cooldownGoalId =
                requireNotNull(dto.cooldownGoalId) {
                    "cooldownGoalId is required for GOAL cooldown"
                },
            goalData = goalData,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDTO(domain: SkillDomain): SkillDTO =
        when (domain) {
            is SkillDomain.Other ->
                SkillDTO(
                    skillId = domain.id,
                    name = domain.name,
                    elementName = domain.type.name,
                    description = domain.description,
                    quote = domain.quote,
                    levelupSkillId = domain.levelupSkillId,
                    cooldown = domain.isCooldown,
                    deckSlot = domain.deckSlot,
                    cooldownType = CooldownType.OTHER.name,
                    cooldownTimeMinutes = null,
                    cooldownUntil = null,
                    cooldownGoalId = null,
                    cooldownOtherDescription = domain.cooldownOtherDescription,
                )

            is SkillDomain.Time ->
                SkillDTO(
                    skillId = domain.id,
                    name = domain.name,
                    elementName = domain.type.name,
                    description = domain.description,
                    quote = domain.quote,
                    levelupSkillId = domain.levelupSkillId,
                    cooldown = domain.isCooldown,
                    deckSlot = domain.deckSlot,
                    cooldownType = CooldownType.TIME.name,
                    cooldownTimeMinutes = domain.cooldownTimeMinutes,
                    cooldownUntil = domain.cooldownUntil?.toEpochMilli(),
                    cooldownGoalId = null,
                    cooldownOtherDescription = null,
                )

            is SkillDomain.Goal ->
                SkillDTO(
                    skillId = domain.id,
                    name = domain.name,
                    elementName = domain.type.name,
                    description = domain.description,
                    quote = domain.quote,
                    levelupSkillId = domain.levelupSkillId,
                    cooldown = domain.isCooldown,
                    deckSlot = domain.deckSlot,
                    cooldownType = CooldownType.GOAL.name,
                    cooldownTimeMinutes = null,
                    cooldownUntil = null,
                    cooldownGoalId = domain.cooldownGoalId,
                    cooldownOtherDescription = null,
                )
        }

    private fun parseSkillType(raw: String?): SkillType =
        raw?.let { runCatching { SkillType.valueOf(it.uppercase()) }.getOrNull() }
            ?: SkillType.RESILIENCE

    private fun resolveIcon(type: SkillType): Int =
        when (type) {
            SkillType.ADAPTABILITY -> R.drawable.ic_skills_adaptability
            SkillType.CONTROL -> R.drawable.ic_skills_control
            SkillType.EMPATHY -> R.drawable.ic_skills_empathy
            SkillType.FOCUS -> R.drawable.ic_skills_focus
            SkillType.MOTIVATION -> R.drawable.ic_skills_motivation
            else -> R.drawable.ic_skills_resilience
        }

    private fun resolveOnThrowSFX(type: SkillType): Int =
        when (type) {
            SkillType.ADAPTABILITY -> R.raw.snd_fx_skill_adaptability
            SkillType.CONTROL -> R.raw.snd_fx_skill_control
            SkillType.EMPATHY -> R.raw.snd_fx_skill_empathy
            SkillType.FOCUS -> R.raw.snd_fx_skill_focus
            SkillType.MOTIVATION -> R.raw.snd_fx_skill_motivation
            else -> R.raw.snd_fx_skill_resilience
        }
}
