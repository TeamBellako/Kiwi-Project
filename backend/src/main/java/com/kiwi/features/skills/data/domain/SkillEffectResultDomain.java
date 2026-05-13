package com.kiwi.features.skills.data.domain;

import com.kiwi.features.combat.data.domain.CombatActiveStatusDomain;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
public class SkillEffectResultDomain {

    private SkillEffectResultType typeResult;

    private CombatActorType target; // USER / ENEMY / ALLY

    private StatType statAffected;

    private Float value;

    private boolean critic = false;

    private CombatActiveStatusDomain appliedStatus;

    private Integer turns;

    private List<Long> resetCooldownSkills;;
}
