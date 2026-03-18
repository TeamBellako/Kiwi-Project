package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatActorStateDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import org.springframework.stereotype.Component;

@Component
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

        CombatPersistence combat = context.getCombat();

        // START TURN STATES
        stateService.onTurnStart(context);

        // USER ACTION
        if(stateService.canAct(context, CombatActorType.USER)) {

            CombatActionDTO action =
                    damageCalculator.executeSkill(
                            context,
                            CombatActorType.USER,
                            userSkillId
                    );

            context.addAction(action);

        } else {
            //TODO REPRESENTAR POR ACCION O ALGO QUE NO SE HA EJECUTADO EL TURNO POR CULPA DEL ESTADO
            // context.addAction(stateService.buildSkipAction(CombatActor.USER));
        }

        // ENEMY ACTION
        if(combat.getEnemyHp() > 0) {

            Long enemySkill =
                    enemyAI.chooseSkill(context);

            if(stateService.canAct(context, CombatActorType.ENEMY)) {

                CombatActionDTO enemyAction =
                        damageCalculator.executeSkill(
                                context,
                                CombatActorType.ENEMY,
                                enemySkill
                        );

                context.addAction(enemyAction);

            } else {
                //TODO REPRESENTAR POR ACCION O ALGO QUE NO SE HA EJECUTADO EL TURNO POR CULPA DEL ESTADO
                // context.addAction(stateService.buildSkipAction(CombatActor.ENEMY));
            }
        }

        // END TURN STATES
        stateService.onTurnEnd(context);

        // TURN UPDATE
        combat.setTurnNumber(combat.getTurnNumber() + 1);

        // CHECK WIN
        if(context.getEnemy().getHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_WON);
        }

        if(context.getUser().getHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
        }

        // BUILD RESULT
        CombatActorStateDTO userState =
                CombatActorStateDTO.builder()
                        .actor("USER")
                        .hp(combat.getUserHp())
                        .activeStates(stateService.getActiveStates(context, CombatActorType.USER))
                        .build();

        CombatActorStateDTO enemyState =
                CombatActorStateDTO.builder()
                        .actor("ENEMY")
                        .hp(combat.getEnemyHp())
                        .activeStates(stateService.getActiveStates(context, CombatActorType.ENEMY))
                        .build();

        return CombatTurnResultDTO.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .user(userState)
                .enemy(enemyState)
                .actions(context.getActions())
                .combatStatus(combat.getCombatStatus().name())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDTO setTimeOut(Long userId, CombatPersistence combat) {

        CombatActorStateDTO userState =
                CombatActorStateDTO.builder()
                        .actor("USER")
                        .hp(combat.getUserHp())
                        .build();

        CombatActorStateDTO enemyState =
                CombatActorStateDTO.builder()
                        .actor("ENEMY")
                        .hp(combat.getEnemyHp())
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

    //------------------------------------------------------------------------------------------------------------------

}