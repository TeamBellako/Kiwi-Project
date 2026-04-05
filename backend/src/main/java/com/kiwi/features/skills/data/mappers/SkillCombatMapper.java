package com.kiwi.features.skills.data.mappers;

import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.skills.data.domain.SkillEffectDomain;
import com.kiwi.features.skills.data.persistence.SkillEffectPersistence;
import com.kiwi.features.skills.data.persistence.SkillPersistence;

import java.util.List;

public class SkillCombatMapper {

    public static SkillCombatDomain toDomain(
            SkillPersistence skill,
            List<SkillEffectPersistence> effects
    ) {
        return SkillCombatDomain.builder()
                .id(skill.getId())
                .name(skill.getName())
                .effects(
                        effects.stream()
                                .map(SkillCombatMapper::toEffectDomain)
                                .toList()
                )
                .build();
    }

    private static SkillEffectDomain toEffectDomain(SkillEffectPersistence e) {
        return SkillEffectDomain.builder()
                .effectType(e.getEffectType())
                .target(e.getTarget())
                .power(e.getPower())
                .attackType(e.getAttackType())
                .elementId(e.getElementId())
                .hitChance(e.getHitChance())
                .stateId(e.getStateId())
                .statusDuration(e.getStatusDuration())
                .build();
    }
}