package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CombatConfigDomain {

    private Long id;

    private Long enemyId;

    private int timeLimit;
}
