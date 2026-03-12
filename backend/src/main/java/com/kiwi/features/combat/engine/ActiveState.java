package com.kiwi.features.combat.engine;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ActiveState {

    private Long stateId;
    private int remainingTurns;
    private float value;
}
