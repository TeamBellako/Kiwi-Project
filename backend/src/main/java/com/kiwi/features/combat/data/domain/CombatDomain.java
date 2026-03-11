package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CombatDomain {

    private Long id;

    private Long combatConfigId;

    private Long userId;
    private Long enemyId;

    private int timeMax;
    private int timeRemaining;

    private int userHp;
    private int enemyHp;

    private int turnNumber;

    private CombatGeneralStatus combatStatus;

    private Instant startedAt;
}