package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class CombatDomain {

    private Long id;

    private Long combatConfigId;

    private Long userId;

    private Long enemyId;

    // USER
    private Integer userHp;
    private Integer userMaxHp;
    private Integer userPatk;
    private Integer userMatk;
    private Integer userPdef;
    private Integer userMdef;
    private Integer userAcc;
    private Integer userEva;
    private Integer userLck;

    // ENEMY
    private Integer enemyHp;
    private Integer enemyMaxHp;
    private Integer enemyPatk;
    private Integer enemyMatk;
    private Integer enemyPdef;
    private Integer enemyMdef;
    private Integer enemyAcc;
    private Integer enemyEva;
    private Integer enemyLck;

    private int turnNumber;

    private Instant endsAt;

    private CombatGeneralStatus combatStatus;

    @Builder.Default
    private List<CombatBarkTriggerDomain> barks = List.of();

    @Builder.Default
    private List<Long> firedBarkIds = List.of();
}