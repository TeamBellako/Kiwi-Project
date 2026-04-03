package com.kiwi.features.skills.data;

public class SkillMapper {

    // --------------------------------------------------------------------------------------------
    // SKILL PERSISTENCE + USER SKILL STATUS → DOMAIN
    // --------------------------------------------------------------------------------------------
    public static SkillDomain toDomain(
            SkillPersistence skill,
            UserSkillStatusPersistence status
    ) {
        return new SkillDomain(
                skill.getId(),
                skill.getName(),
                skill.getDescription(),
                skill.getQuote(),
                skill.getSkillType(),

                skill.getCooldownType(),
                skill.getCooldownGoalId(),
                skill.getCooldownTimeMinutes(),
                skill.getCooldownOtherDescription(),
                skill.getLevelupSkillId(),

                status != null && status.isCooldown(),
                status != null ? status.getCooldownUntil() : null,
                status != null ? status.getDeckSlot() : 0
        );
    }

    // --------------------------------------------------------------------------------------------
    // SKILL DOMAIN → DTO
    // --------------------------------------------------------------------------------------------
    public static SkillDTO toDTO(SkillDomain domain) {
        SkillDTO dto = new SkillDTO();
        dto.setSkillId(domain.getSkillId());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        dto.setQuote(domain.getQuote());
        dto.setType(domain.getType().name());

        dto.setCooldownType(domain.getCooldownType().name());
        dto.setCooldownGoalId(domain.getCooldownGoalId());
        dto.setCooldownTimeMinutes(domain.getCooldownTimeMinutes());
        dto.setCooldownOtherDescription(domain.getCooldownOtherDescription());
        dto.setLevelupSkillId(domain.getLevelupSkillId());

        dto.setCooldown(domain.isCooldown());
        dto.setCooldownUntil(domain.getCooldownUntil() != null
                ? domain.getCooldownUntil().toEpochMilli()
                : null);
        dto.setDeckSlot(domain.getDeckSlot());

        return dto;
    }

    // --------------------------------------------------------------------------------------------
    // SKILL DOMAIN → USER SKILL STATUS PERSISTENCE
    // --------------------------------------------------------------------------------------------
    public static UserSkillStatusPersistence toPersistence(
            Long userId,
            SkillDomain domain,
            SkillPersistence skillPersistence
    ) {
        UserSkillStatusPersistence persistence = new UserSkillStatusPersistence();
        persistence.setId(new UserSkillStatusKey(userId, domain.getSkillId()));
        persistence.setCooldown(domain.isCooldown());
        persistence.setCooldownUntil(domain.getCooldownUntil());
        persistence.setDeckSlot(domain.getDeckSlot());
        persistence.setSkill(skillPersistence);
        return persistence;
    }
}
