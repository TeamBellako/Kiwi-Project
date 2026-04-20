package com.kiwi.features.skills.data.domain;

import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.StatModificationType;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.skills.data.enums.SkillEffectTargetType;
import com.kiwi.features.skills.data.enums.SkillEffectType;
import lombok.*;

@Getter
@Builder
public class SkillEffectDomain {

    private SkillEffectType effectType;

    private SkillEffectTargetType target;

    private StatType statAffected;

    private StatModificationType statModification;

    private Float power;

    private AttackType attackType;

    private Long elementId;

    private Integer hitChance;

    private Long stateId;

    private Integer statusDuration;

}