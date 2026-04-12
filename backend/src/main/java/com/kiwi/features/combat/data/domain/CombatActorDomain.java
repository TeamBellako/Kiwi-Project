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

    private int hp;
    private int maxHp;

    private int patk;
    private int matk;

    private int pdef;
    private int mdef;

    private int acc;
    private int eva;
    private int lck;

    private Map<Long, ElementMultiplierDomain> elementMultipliers;
    private Map<Long, StatusResistanceDomain> statusResistances;

    private List<CombatActiveStatusDomain> states;

    @Setter
    private  Map<Long, SkillCombatDomain> skills;

    @Setter
    private CombatActionType actionModifierType;

    @Setter
    private Long lastSkillUsed;

    public void damage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

}