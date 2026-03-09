package com.kiwi.features.combat.data.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserElementMultiplierDomain {

    private Long userId;
    private Long elementId;
    private float multiplier;
}