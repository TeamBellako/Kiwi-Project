package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnemyElementMultiplierDomain {

    private Long enemyId;

    private Long elementId;

    private float multiplier;
}