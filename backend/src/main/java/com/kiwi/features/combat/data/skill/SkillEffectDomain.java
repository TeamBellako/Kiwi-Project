package com.kiwi.features.combat.data.skill;

import com.kiwi.features.combat.data.AttackType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillEffectDomain {

    private Long id;

    private Long skillId;

    private SkillEffectType effectType;

    private Float power;

    private AttackType attackType;

    private Long elementId;

    private Integer hitChance;

    private Long stateId;

    private Integer statusDuration;
}