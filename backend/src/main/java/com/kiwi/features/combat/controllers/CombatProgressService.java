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

    public void updateTimeOut(CombatDomain combatDomain) {

        if (combatDomain.getEndsAt() != null) {
            Instant now = Instant.now();

            if (combatDomain.getEndsAt().isBefore(now)) {
                combatDomain.setCombatStatus(CombatGeneralStatus.USER_LOST);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void updateAbandon(CombatDomain combatDomain) {

        combatDomain.setCombatStatus(CombatGeneralStatus.USER_LOST);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void applyTurnResult(
            CombatPersistence combat,
            CombatDomain combatDomain

    ) {
        CombatActorDomain user = combatDomain.getUser();
        CombatActorDomain enemy = combatDomain.getEnemy();

        if(user != null) {
            combat.setUserHp(user.getStats().getCurrentHp());
            combat.setUserMaxHp(user.getStats().getMaxHp());
            combat.setUserPatk(user.getStats().getPatk());
            combat.setUserMatk(user.getStats().getMatk());
            combat.setUserPdef(user.getStats().getPdef());
            combat.setUserMdef(user.getStats().getMdef());
            combat.setUserAcc(user.getStats().getAcc());
            combat.setUserEva(user.getStats().getEva());
            combat.setUserLck(user.getStats().getLck());
            combat.setUserShield(user.getStats().getShield());
            combat.setUserTurns(user.getStats().getTurns());
        }

        if(enemy != null) {
            combat.setEnemyHp(enemy.getStats().getCurrentHp());
            combat.setEnemyMaxHp(enemy.getStats().getMaxHp());
            combat.setEnemyPatk(enemy.getStats().getPatk());
            combat.setEnemyMatk(enemy.getStats().getMatk());
            combat.setEnemyPdef(enemy.getStats().getPdef());
            combat.setEnemyMdef(enemy.getStats().getMdef());
            combat.setEnemyAcc(enemy.getStats().getAcc());
            combat.setEnemyEva(enemy.getStats().getEva());
            combat.setEnemyLck(enemy.getStats().getLck());
            combat.setEnemyShield(enemy.getStats().getShield());
            combat.setEnemyTurns(enemy.getStats().getTurns());
        }

        combat.setCombatStatus(combatDomain.getCombatStatus());
        combat.setTurnNumber(combatDomain.getTurnNumber());
    }

    //------------------------------------------------------------------------------------------------------------------

}
