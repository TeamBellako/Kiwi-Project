package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatStatusAppliedDTO {

    private Long stateId;

    private String name;

    private int icon;

    private String description;

    private int remainingTurns;

    private Float value;
}