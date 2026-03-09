package com.kiwi.features.combat.data.user;

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