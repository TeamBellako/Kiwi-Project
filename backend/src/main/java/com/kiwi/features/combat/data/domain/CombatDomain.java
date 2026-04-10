package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
public class CombatDomain {

    private Long id;

    private Long combatConfigId;

    private Long userId;

    private Long enemyId;

    private Integer userHp;

    private Integer enemyHp;

    private int turnNumber;

    private Instant endsAt;

    private CombatGeneralStatus combatStatus;
}