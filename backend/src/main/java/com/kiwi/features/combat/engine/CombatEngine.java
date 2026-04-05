package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.controllers.CombatStateService;
import com.kiwi.features.combat.data.domain.ActorDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.ActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

        // USER TURN
        ActorDomain userActor = context.getActor(CombatActorType.USER);

        //APPLY CURRENT STATES
        stateService.applyActiveStatesToActor(userActor,context);

        //REDUCE STATES TURNS
        stateService.reduceStatesTurnsToActor(userActor, context);

        //CHECK USER LIFE
        if(combat.getUserHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
            return buildCombatTurnResultDTO(context, combat);
        }

        //EXECUTE ACTION
        if (userActor.getActionModifierType() != ActionType.ACTOR_BLOCKED_BY_STATE) {
            CombatActionDTO action;
            if ( userActor.getActionModifierType() == ActionType.SKILL_REPEAT_BY_STATE) {

                action = damageCalculator.executeSkill(
                        context,
                        CombatActorType.USER,
                        userActor.getLastSkillUsed()
                );

            } else {
                action = damageCalculator.executeSkill(
                        context,
                        CombatActorType.USER,
                        userSkillId
                );

            }
            context.addAction(action);
        }

        // ENEMY TURN
        if(combat.getEnemyHp() > 0) {

            ActorDomain enemyActor = context.getActor(CombatActorType.ENEMY);

            //APPLY CURRENT STATES
            stateService.applyActiveStatesToActor(enemyActor,context);

            //REDUCE STATES TURNS
            stateService.reduceStatesTurnsToActor(enemyActor, context);

            //CHECK ENEMY LIFE
            if(combat.getEnemyHp() <= 0) {
                combat.setCombatStatus(CombatGeneralStatus.USER_WON);
                return buildCombatTurnResultDTO(context, combat);
            }

            //CHOOSE SKILL
            Long enemySkill = enemyAI.chooseSkill(context);

            //EXECUTE ACTION
            if (enemyActor.getActionModifierType() != ActionType.ACTOR_BLOCKED_BY_STATE) {
                CombatActionDTO action;
                if (enemyActor.getActionModifierType() == ActionType.SKILL_REPEAT_BY_STATE) {

                    action = damageCalculator.executeSkill(
                            context,
                            CombatActorType.ENEMY,
                            enemyActor.getLastSkillUsed()
                    );

                } else {
                    action = damageCalculator.executeSkill(
                            context,
                            CombatActorType.ENEMY,
                            enemySkill
                    );

                }
                context.addAction(action);
            }
        }

        // TURN UPDATE
        combat.setTurnNumber(combat.getTurnNumber() + 1);

        // CHECK COMBAT END
        if(context.getUser().getHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
        }
        if(context.getEnemy().getHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_WON);
        }

        return buildCombatTurnResultDTO(context, combat);
    }

    //------------------------------------------------------------------------------------------------------------------

    private CombatTurnResultDTO buildCombatTurnResultDTO(CombatContext context, CombatPersistence combat)
    {
        return CombatTurnResultDTO.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(context.getActions())
                .combatStatus(combat.getCombatStatus().name())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDTO buildTimeoutCombatTurnResultDTO(Long userId, CombatPersistence combat) {

        List<CombatActionDTO> actions = new ArrayList<>();

        CombatActionDTO action =
                CombatActionDTO.builder()
                        .actor(CombatActorType.USER.name())
                        .actionType(ActionType.TIMEOUT.name())
                        .build();

        actions.add(action);

        return CombatTurnResultDTO.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(actions)
                .combatStatus(CombatGeneralStatus.USER_LOST.toString())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}