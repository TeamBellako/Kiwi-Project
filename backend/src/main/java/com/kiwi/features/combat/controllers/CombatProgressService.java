package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CombatProgressService {

    //------------------------------------------------------------------------------------------------------------------

    public void updateTimeOut(CombatDomain combat) {

        if (combat.getEndsAt() != null) {
            Instant now = Instant.now();

            if (combat.getEndsAt().isBefore(now)) {
                combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void applyTurnResult(
            CombatPersistence combat,
            CombatDomain domain,
            CombatActorDomain user,
            CombatActorDomain enemy
    ) {
        combat.setUserHp(user.getHp());
        combat.setEnemyHp(enemy.getHp());
        combat.setCombatStatus(domain.getCombatStatus());
        combat.setTurnNumber(domain.getTurnNumber());
    }

    //------------------------------------------------------------------------------------------------------------------

}
