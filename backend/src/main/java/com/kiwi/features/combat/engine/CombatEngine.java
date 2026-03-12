package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatActorStateDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.CombatActor;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;

import java.util.ArrayList;
import java.util.List;

public class CombatEngine {

    private final CombatDamageCalculator damageCalculator;
    private final CombatStateService stateService;
    private final EnemyAI enemyAI;

    //------------------------------------------------------------------------------------------------------------------

    public CombatEngine(
            CombatDamageCalculator damageCalculator,
            CombatStateService stateService,
            EnemyAI enemyAI
    ) {
        this.damageCalculator = damageCalculator;
        this.stateService = stateService;
        this.enemyAI = enemyAI;
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDTO executeTurn(
            CombatContext context,
            Long userSkillId
    ) {

        // USER ACTION
        if(stateService.canAct(context.getUser())) {

            CombatActionDTO action =
                    damageCalculator.executeSkill(
                            context,
                            CombatActor.USER,
                            userSkillId
                    );

            context.addAction(action);

        } else {
            context.addAction(stateService.buildSkipAction(CombatActor.USER));
        }

        // ENEMY ACTION
        if(context.getCombat().getEnemyHp() > 0) {

            Long enemySkill =
                    enemyAI.chooseSkill(context);

            if(stateService.canAct(context.getEnemy())) {

                CombatActionDTO enemyAction =
                        damageCalculator.executeSkill(
                                context,
                                CombatActor.ENEMY,
                                enemySkill
                        );

                context.addAction(enemyAction);

            } else {

                context.addAction(
                        stateService.buildSkipAction(CombatActor.ENEMY)
                );
            }
        }

        // APPLY STATES
        stateService.applyCurrentStates(context);

        // UPDATE STATES (CHECK IF LAST TURN AND REMOVE)
        stateService.updateStates(context);

        // TURN UPDATE
        context.getCombat().setTurnNumber( context.getCombat().getTurnNumber() + 1);

        // CHECK WIN
        if(context.getEnemy().getHp() <= 0) {
            context.getCombat().setCombatStatus(CombatGeneralStatus.USER_WON);
        }

        if(context.getUser().getHp() <= 0) {
            context.getCombat().setCombatStatus(CombatGeneralStatus.USER_LOST);
        }

        // BUILD RESULT
        CombatActorStateDTO userState =
                CombatActorStateDTO.builder()
                        .actor("USER")
                        .hp(combat.getUserHp())
                        .activeStates(stateService.getActiveStates(combat, CombatActor.USER))
                        .build();

        CombatActorStateDTO enemyState =
                CombatActorStateDTO.builder()
                        .actor("ENEMY")
                        .hp(combat.getEnemyHp())
                        .activeStates(stateService.getActiveStates(combat, CombatActor.ENEMY))
                        .build();

        return CombatTurnResultDTO.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .user(userState)
                .enemy(enemyState)
                .actions(actions)
                .combatStatus(combat.getCombatStatus().name())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDTO setTimeOut(Long userId, CombatPersistence combat) {

        CombatActorStateDTO userState =
                CombatActorStateDTO.builder()
                        .actor("USER")
                        .hp(combat.getUserHp())
                        .activeStates(stateService.getActiveStates(combat, CombatActor.USER))
                        .build();

        CombatActorStateDTO enemyState =
                CombatActorStateDTO.builder()
                        .actor("ENEMY")
                        .hp(combat.getEnemyHp())
                        .activeStates(stateService.getActiveStates(combat, CombatActor.ENEMY))
                        .build();

        return CombatTurnResultDTO.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .user(userState)
                .enemy(enemyState)
                .actions(null)
                .combatStatus(CombatGeneralStatus.USER_LOST.toString())
                .build();

    }
}