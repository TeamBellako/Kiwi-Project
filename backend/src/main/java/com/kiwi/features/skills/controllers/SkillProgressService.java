package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.*;

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
        return SkillMapper.toDomain(skill, status);
    }

    public long resolveLevelUpSkillId(SkillDomain skill) {
        if (skill.getLevelupSkillId() == null) {
            throw new IllegalStateException("Skill cannot be leveled up");
        }
        return skill.getLevelupSkillId();
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

    public SkillDomain removeCooldown(SkillDomain skill) {
        skill.setCooldown(false);
        skill.setCooldownUntil(null);
        return skill;
    }
}
