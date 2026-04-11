package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatActiveStatusDTO {

    private Long stateId;

    private String name;

    private Integer icon;

    private String description;

    private Integer remainingTurns;

    private Float value;
}