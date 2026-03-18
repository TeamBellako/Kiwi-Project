package com.kiwi.features.combat.engine;

import lombok.*;

@Builder
@Getter
@Setter
public class ActiveState {

    private Long stateId;
    private int remainingTurns;
    private float value;
}
