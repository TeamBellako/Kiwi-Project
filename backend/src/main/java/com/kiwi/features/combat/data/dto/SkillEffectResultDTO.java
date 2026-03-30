package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillEffectResultDTO {

    private String type; // DAMAGE / HEAL / APPLY_STATUS / MISS

    private String target; // USER / ENEMY / ALLY

    private Float value;

    private boolean critic = false;

    private CombatStateAppliedDTO stateApplied;
}