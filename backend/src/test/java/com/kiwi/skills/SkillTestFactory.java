package com.kiwi.skills;

import com.kiwi.features.skills.data.*;

import java.time.Instant;

public class SkillTestFactory {

    public static SkillPersistence persistenceSkill(Long id) {
        SkillPersistence s = new SkillPersistence();
        s.setName("Skill " + id);
        s.setDescription("Description " + id);
        s.setQuote("Quote " + id);
        s.setIcon(1);

        s.setCooldownType(CooldownType.TIME);
        s.setCooldownTimeMinutes(10);
        s.setCooldownGoalId(null);
        s.setCooldownOtherDescription(null);
        s.setLevelupSkillId(null);

        return s;
    }

    private static UserSkillStatusPersistence userSkill(
            Long userId,
            SkillPersistence skill,
            boolean isCooldown,
            int deckSlot
    ) {
        UserSkillStatusPersistence s = new UserSkillStatusPersistence();
        s.setId(new UserSkillStatusKey(userId, skill.getId()));
        s.setCooldown(isCooldown);
        s.setCooldownUntil(isCooldown ? Instant.now().plusSeconds(600) : null);
        s.setDeckSlot(deckSlot);
        s.setSkill(skill);
        return s;
    }

    public static UserSkillStatusPersistence equippedSkill(
            Long userId,
            SkillPersistence skill
    ) {
        return userSkill(userId,skill,false,1);
    }

    public static UserSkillStatusPersistence unEquippedSkill(
            Long userId,
            SkillPersistence skill
            ) {
        return userSkill(userId,skill,false,0);
    }

    public static UserSkillStatusPersistence cooldownSkill(
            Long userId,
            SkillPersistence skill
    ) {
        return userSkill(userId,skill,true,1);
    }

    public static SkillDTO skillDto(
            Long id,
            boolean isCooldown,
            int deckSlot
    ) {
        return SkillDTO.builder()
                .skillId(id)
                .name("Skill " + id)
                .description("Desc")
                .quote("Quote")
                .icon(1)

                .cooldownType("OTHER")
                .cooldownGoalId(null)
                .cooldownTimeMinutes(null)
                .cooldownOtherDescription("Cooldown by quest")
                .levelupSkillId(null)

                .isCooldown(isCooldown)
                .cooldownUntil(null)
                .deckSlot(deckSlot)
                .build();
    }

    public static EquipSkillDTO equipSkillDTO(
            int deckSlot
    ){
        return EquipSkillDTO.builder()
                .deckSlot(deckSlot).build();
    }
}
