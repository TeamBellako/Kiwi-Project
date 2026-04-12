package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
import lombok.*;

@Builder
@Getter
@Setter
public class SkillEffectResultDomain {

    private SkillEffectResultType typeResult; // DAMAGE / HEAL / STATUS_APPLIED / STATUS_REMOVED / MISS

    private CombatActorType target; // USER / ENEMY / ALLY

    private Float value;

    private boolean critic = false;

    private CombatActiveStatusDomain appliedStatus;

}
