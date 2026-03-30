package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnemyStatusResistanceDomain {

    private Long enemyId;

    private Long stateId;

    private float resistance;
}