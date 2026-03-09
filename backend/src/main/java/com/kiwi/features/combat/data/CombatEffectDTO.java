package com.kiwi.features.combat.data;

import com.kiwi.features.combat.data.state.CombatStateAppliedDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatEffectDTO {

    private String type; // DAMAGE / HEAL / STATUS

    private String target;

    private Float value;

    private boolean crit;

    private CombatStateAppliedDTO appliedState;
}