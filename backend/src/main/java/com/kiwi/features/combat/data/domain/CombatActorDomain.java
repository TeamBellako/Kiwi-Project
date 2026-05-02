package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CombatActorDomain {

    private CombatActorType type;

    private StatsDomain stats;

    private Map<Long, ElementMultiplierDomain> elementMultipliers;
    private Map<Long, StatusResistanceDomain> statusResistances;

    private List<CombatActiveStatusDomain> activeStatuses;

    @Setter
    private  Map<Long, SkillCombatDomain> skills;

    @Setter
    private  List<Long> blockedSkills;

    @Setter
    private CombatActionType actionModifierType;

    @Setter
    private Long lastSkillUsed;

    public void damage(int amount) {

        stats.setCurrentHp(Math.max(0, stats.getCurrentHp() - amount));
    }

    public void heal(int amount) {

        stats.setCurrentHp(Math.min(stats.getMaxHp(), stats.getCurrentHp() + amount));
    }

}