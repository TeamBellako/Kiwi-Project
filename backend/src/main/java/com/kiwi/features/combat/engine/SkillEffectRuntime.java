package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.SkillEffectType;
import lombok.*;

@Getter
@Builder
public class SkillEffectRuntime {

    private SkillEffectType effectType;

    private Float power;

    private AttackType attackType;

    private Long elementId;

    private Integer hitChance;

    private Long stateId;

    private Integer statusDuration;

}