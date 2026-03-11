package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusResistanceDomain {

    private Long userId;
    private Long stateId;
    private float resistance;
}