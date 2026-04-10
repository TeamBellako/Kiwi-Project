package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusResistanceDomain {

    private Long stateId;

    private String stateName;

    private String stateDescription;

    private int stateIcon;

    private float resistance;
}
