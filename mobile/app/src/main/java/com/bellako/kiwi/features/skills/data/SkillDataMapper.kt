package com.bellako.kiwi.features.skills.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

object SkillDataMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomainWithoutGoal(dto: SkillDTO): SkillDomain =
        when (CooldownType.valueOf(dto.cooldownType)) {
            CooldownType.OTHER ->
                SkillDomain.Other(
                    id = dto.skillId,
                    name = dto.name,
                    description = dto.description,
                    quote = dto.quote,
                    icon = dto.icon,
                    levelupSkillId = dto.levelupSkillId,
                    isCooldown = dto.cooldown,
                    deckSlot = dto.deckSlot,
                    cooldownOtherDescription =
                        requireNotNull(dto.cooldownOtherDescription) {
                            "cooldownOtherDescription is required for OTHER cooldown"
                        },
                )

            CooldownType.TIME ->
                SkillDomain.Time(
                    id = dto.skillId,
                    name = dto.name,
                    description = dto.description,
                    quote = dto.quote,
                    icon = dto.icon,
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
            description = dto.description,
            quote = dto.quote,
            icon = dto.icon,
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
                    description = domain.description,
                    quote = domain.quote,
                    icon = domain.icon,
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
                    description = domain.description,
                    quote = domain.quote,
                    icon = domain.icon,
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
                    description = domain.description,
                    quote = domain.quote,
                    icon = domain.icon,
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
}
