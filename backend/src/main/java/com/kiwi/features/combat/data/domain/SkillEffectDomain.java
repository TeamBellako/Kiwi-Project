package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.SkillEffectTargetType;
import com.kiwi.features.combat.data.enums.SkillEffectType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillEffectDomain {

    private Long id;

    private Long skillId;

    private SkillEffectTargetType target;

    private SkillEffectType effectType;

    private Float power;

    private AttackType attackType;

    private Long elementId;

    private Integer hitChance;

    private Long stateId;

    private Integer statusDuration;
}