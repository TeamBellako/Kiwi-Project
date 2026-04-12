package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class CombatActionDomain {

    private CombatActorType actor;     // USER / ENEMY

    private CombatActionType actionType; // ActionType enum

    private CombatActiveStatusDomain state;
    private Float stateEffectValue;
    private List<Long> blockedSkills;

    private String skillName;
    private List<SkillEffectResultDomain> skillEffectsResults;

}