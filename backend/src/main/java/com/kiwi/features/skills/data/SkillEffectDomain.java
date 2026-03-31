package com.kiwi.features.skills.data;

import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.SkillEffectTargetType;
import com.kiwi.features.combat.data.enums.SkillEffectType;
import lombok.*;

@Getter
@Builder
public class SkillEffectDomain {

    private SkillEffectType effectType;

    private SkillEffectTargetType target;

    private Float power;

    private AttackType attackType;

    private Long elementId;

    private Integer hitChance;

    private Long stateId;

    private String stateName;

    private Integer statusDuration;

}