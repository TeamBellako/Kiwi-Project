package com.bellako.kiwi.features.skills.data

object SkillDataMapper {
    fun toDomain(dto: SkillDTO): SkillDomain =
        SkillDomain(
            id = dto.skillId,
            name = dto.name,
            description = dto.description,
            quote = dto.quote,
            icon = dto.icon,
            cooldownType = CooldownType.valueOf(dto.cooldownType),
            cooldownGoalId = dto.cooldownGoalId,
            cooldownTimeMinutes = dto.cooldownTimeMinutes,
            cooldownOtherDescription = dto.cooldownOtherDescription,
            levelupSkillId = dto.levelupSkillId,
            isCooldown = dto.isCooldown,
            cooldownUntil = dto.cooldownUntil,
            deckSlot = dto.deckSlot,
        )

    fun toDto(domain: SkillDomain): SkillDTO =
        SkillDTO(
            skillId = domain.id,
            name = domain.name,
            description = domain.description,
            quote = domain.quote,
            icon = domain.icon,
            cooldownType = domain.cooldownType.name,
            cooldownGoalId = domain.cooldownGoalId,
            cooldownTimeMinutes = domain.cooldownTimeMinutes,
            cooldownOtherDescription = domain.cooldownOtherDescription,
            levelupSkillId = domain.levelupSkillId,
            isCooldown = domain.isCooldown,
            cooldownUntil = domain.cooldownUntil,
            deckSlot = domain.deckSlot,
        )
}
