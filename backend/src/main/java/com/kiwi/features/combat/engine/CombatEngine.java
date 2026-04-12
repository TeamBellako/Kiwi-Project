package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.controllers.CombatStateService;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CombatEngine {

    private final CombatDamageCalculator damageCalculator;
    private final CombatStatusManager statusManager;
    private final EnemyAI enemyAI;

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDTO executeTurn(
            CombatContext context,
            Long userSkillId
    ) {

        CombatDomain combat = context.getCombat();

        // USER TURN
        CombatActorDomain userActor = context.getActor(CombatActorType.USER);

        //APPLY CURRENT STATES
        statusManager.applyActiveStatesEffectsToActor(userActor,context);

        //CHECK USER LIFE
        if(combat.getUserHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
            return buildCombatTurnResultDTO(context, combat);
        }

        //EXECUTE ACTION
        if (userActor.getActionModifierType() != CombatActionType.ACTOR_BLOCKED_BY_STATE) {
            CombatActionDTO action;
            if ( userActor.getActionModifierType() == CombatActionType.SKILL_REPEAT_BY_STATE) {

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

        //REDUCE STATES TURNS
        statusManager.reduceStatesTurnsToActor(userActor, context);

        // ENEMY TURN
        if(combat.getEnemyHp() > 0) {

            CombatActorDomain enemyActor = context.getActor(CombatActorType.ENEMY);

            //APPLY CURRENT STATES
            statusManager.applyActiveStatesEffectsToActor(enemyActor,context);

            //CHECK ENEMY LIFE
            if(combat.getEnemyHp() <= 0) {
                combat.setCombatStatus(CombatGeneralStatus.USER_WON);
                return buildCombatTurnResultDTO(context, combat);
            }

            //CHOOSE SKILL
            Long enemySkill = enemyAI.chooseSkill(context);

            //EXECUTE ACTION
            if (enemyActor.getActionModifierType() != CombatActionType.ACTOR_BLOCKED_BY_STATE) {
                CombatActionDTO action;
                if (enemyActor.getActionModifierType() == CombatActionType.SKILL_REPEAT_BY_STATE) {

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

            //REDUCE STATES TURNS
            statusManager.reduceStatesTurnsToActor(enemyActor, context);
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

    private CombatTurnResultDTO buildCombatTurnResultDTO(CombatContext context, CombatDomain combat)
    {
        return CombatTurnResultDTO.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(context.getActionsDTOs())
                .combatStatus(combat.getCombatStatus().name())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDTO buildTimeoutCombatTurnResultDTO(Long userId, CombatDomain combat) {

        List<CombatActionDTO> actions = new ArrayList<>();

        CombatActionDTO action =
                CombatActionDTO.builder()
                        .actor(CombatActorType.USER.name())
                        .actionType(CombatActionType.TIMEOUT.name())
                        .build();

        actions.add(action);

        return CombatTurnResultDTO.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(actions)
                .combatStatus(combat.getCombatStatus().name())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}