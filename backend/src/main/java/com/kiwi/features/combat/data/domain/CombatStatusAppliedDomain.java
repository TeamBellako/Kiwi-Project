package com.kiwi.features.combat.data.domain;

import lombok.*;

@Builder
@Getter
@Setter
public class CombatStatusAppliedDomain {

    private Long stateId;
    private String name;
    private int remainingTurns;
    private float value;
}
