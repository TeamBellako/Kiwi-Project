package com.kiwi.features.combat.data.domain;

import lombok.*;

@Builder
@Getter
@Setter
public class CombatActiveStatusDomain {

    private Long stateId;

    private String name;

    private int icon;

    private String description;

    private int remainingTurns;

    private Float value;
}
