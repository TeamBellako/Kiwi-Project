package com.kiwi.features.combat.data.domain;


import com.kiwi.features.combat.data.enums.CombatActor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatStatusEffectDomain {

    private Long id;

    private Long combatId;

    private Long sourceSkillId;

    private CombatActor target;

    private Long stateId;

    private Float value;

    private int remainingTurns;
}