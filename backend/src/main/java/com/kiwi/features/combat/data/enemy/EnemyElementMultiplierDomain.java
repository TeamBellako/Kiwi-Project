package com.kiwi.features.combat.data.enemy;

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