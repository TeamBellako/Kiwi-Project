package com.bellako.kiwi.features.skills.data

object SkillDataMapper {
    fun toDomain(dto: SkillDTO): SkillDomain =
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
                    cooldownUntil = dto.cooldownUntil,
                )

            CooldownType.GOAL ->
                SkillDomain.Goal(
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
                    // Filled in viewmodel
                    goalAction = "",
                    goalProgress = 0,
                    goalTarget = 0,
                )
        }

    fun toDto(domain: SkillDomain): SkillDTO =
        when (domain) {
            is SkillDomain.Other ->
                SkillDTO(
                    skillId = domain.id,
                    name = domain.name,
                    description = domain.description,
                    quote = domain.quote,
                    icon = domain.icon,
                    cooldownType = CooldownType.OTHER.name,
                    cooldownGoalId = null,
                    cooldownTimeMinutes = null,
                    cooldownOtherDescription = domain.cooldownOtherDescription,
                    levelupSkillId = domain.levelupSkillId,
                    cooldown = domain.isCooldown,
                    cooldownUntil = null,
                    deckSlot = domain.deckSlot,
                )

            is SkillDomain.Time ->
                SkillDTO(
                    skillId = domain.id,
                    name = domain.name,
                    description = domain.description,
                    quote = domain.quote,
                    icon = domain.icon,
                    cooldownType = CooldownType.TIME.name,
                    cooldownGoalId = null,
                    cooldownTimeMinutes = domain.cooldownTimeMinutes,
                    cooldownOtherDescription = null,
                    levelupSkillId = domain.levelupSkillId,
                    cooldown = domain.isCooldown,
                    cooldownUntil = domain.cooldownUntil,
                    deckSlot = domain.deckSlot,
                )

            is SkillDomain.Goal ->
                SkillDTO(
                    skillId = domain.id,
                    name = domain.name,
                    description = domain.description,
                    quote = domain.quote,
                    icon = domain.icon,
                    cooldownType = CooldownType.GOAL.name,
                    cooldownGoalId = domain.cooldownGoalId,
                    cooldownTimeMinutes = null,
                    cooldownOtherDescription = null,
                    levelupSkillId = domain.levelupSkillId,
                    cooldown = domain.isCooldown,
                    cooldownUntil = null,
                    deckSlot = domain.deckSlot,
                )
        }
}
