package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.domain.SkillDomain;
import com.kiwi.features.skills.data.mappers.SkillMapper;
import com.kiwi.features.skills.data.persistence.SkillPersistence;
import com.kiwi.features.skills.data.persistence.UserSkillStatusPersistence;

import com.kiwi.features.skills.data.enums.CooldownType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SkillProgressService {

    // ============================================================================================
    // GIVE / LEVEL UP
    // ============================================================================================

    public SkillDomain giveSkill(SkillPersistence skill, UserSkillStatusPersistence status) {

        if (status != null) {
            throw new IllegalStateException("Skill already owned by user");
        }
        return SkillMapper.toDomain(skill, null);
    }

    // ============================================================================================
    // COOLDOWN
    // ============================================================================================

    public SkillDomain putOnCooldown(SkillDomain skill) {

        if (skill.isCooldown()) {
            throw new IllegalStateException("Skill already on cooldown");
        }

        skill.setCooldown(true);

        if (skill.getCooldownType() == CooldownType.TIME) {
            Instant until = Instant.now()
                    .plus(skill.getCooldownTimeMinutes(), ChronoUnit.MINUTES);
            skill.setCooldownUntil(until);
        } else {
            skill.setCooldownUntil(null);
        }

        return skill;
    }

    public SkillDomain updateCooldown(SkillDomain skill) {

        if (skill.getCooldownUntil() == null) {
            return skill;
        }

        Instant now = Instant.now();

        if(skill.getCooldownUntil().isBefore(now)) {
            skill.setCooldown(false);
            skill.setCooldownUntil(null);
        }

        return skill;
    }

    public SkillDomain removeCooldown(SkillDomain skill) {

        skill.setCooldown(false);
        skill.setCooldownUntil(null);
        return skill;
    }

    // ============================================================================================
    // EQUIP
    // ============================================================================================

    public SkillDomain equipSkill(SkillDomain skill, int deckSlot) {

        if (skill.getDeckSlot() != 0) {
            throw new IllegalStateException("Skill already on equipped");
        }

        skill.setDeckSlot(deckSlot);

        return skill;
    }

    public SkillDomain unequipSkill(SkillDomain skill) {

        if (skill.getDeckSlot() == 0) {
            throw new IllegalStateException("Skill already on unequipped");
        }

        skill.setDeckSlot(0);

        return skill;
    }

}
