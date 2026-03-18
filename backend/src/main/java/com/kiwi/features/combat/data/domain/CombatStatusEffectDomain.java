package com.kiwi.features.combat.data.domain;


import com.kiwi.features.combat.data.enums.CombatActorType;
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

    private CombatActorType target;

    private Long stateId;

    private Float value;

    private int remainingTurns;
}