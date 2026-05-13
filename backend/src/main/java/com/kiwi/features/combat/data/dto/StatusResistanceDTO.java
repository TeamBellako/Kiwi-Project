package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusResistanceDTO {

    private Long stateId;

    private String stateName;

    private String stateDescription;

    private int stateIcon;

    private float resistance;
}