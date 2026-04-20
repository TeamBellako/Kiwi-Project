package com.kiwi.features.skills.data.DTO;

import com.kiwi.features.combat.data.dto.CombatActiveStatusDTO;
import com.kiwi.features.combat.data.enums.StatType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillEffectResultDTO {

    private String typeResult; // DAMAGE / HEAL /  MODIFY_STAT / STATUS_APPLIED / STATUS_REMOVED / MISS

    private String target; // USER / ENEMY / ALLY

    private String statAffected;

    private Float value;

    private boolean critic = false;

    private CombatActiveStatusDTO appliedStatus;
}