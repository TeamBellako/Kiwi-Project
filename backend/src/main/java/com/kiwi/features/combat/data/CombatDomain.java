package com.kiwi.features.combat.data;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CombatDomain {

    private Long combatId;

    private Long userId;
    private Long enemyId;

    private int userHp;
    private int enemyHp;

    private int turnNumber;
    private CombatActor turnOwner;

    private CombatGeneralStatus combatStatus;

    private int timeRemaining;
}