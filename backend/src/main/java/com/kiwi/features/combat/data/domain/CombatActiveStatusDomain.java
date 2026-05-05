package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.StatType;
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

    private StatType statAffected;
}
