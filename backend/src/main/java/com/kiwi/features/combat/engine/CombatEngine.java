package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.CombatActionDomain;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.domain.CombatTurnResultDomain;
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

    private final SkillsManager skillsManager;
    private final CombatStatusManager statusManager;
    private final EnemyAI enemyAI;

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDomain executeTurn(
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
            return buildCombatTurnResult(context, combat);
        }

        //EXECUTE ACTION
        if (userActor.getActionModifierType() != CombatActionType.ACTOR_BLOCKED_BY_STATE) {
            CombatActionDomain action;
            if ( userActor.getActionModifierType() == CombatActionType.SKILL_REPEAT_BY_STATE) {

                action = skillsManager.executeSkill(
                        context,
                        CombatActorType.USER,
                        userActor.getLastSkillUsed()
                );

            } else {
                action = skillsManager.executeSkill(
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
        if(context.getEnemy().getStats().getCurrentHp() > 0) {

            CombatActorDomain enemyActor = context.getActor(CombatActorType.ENEMY);

            //APPLY CURRENT STATES
            statusManager.applyActiveStatesEffectsToActor(enemyActor,context);

            //CHECK ENEMY LIFE
            if(combat.getEnemyHp() <= 0) {
                combat.setCombatStatus(CombatGeneralStatus.USER_WON);
                return buildCombatTurnResult(context, combat);
            }

            //CHOOSE SKILL
            Long enemySkill = enemyAI.chooseSkill(context);

            //EXECUTE ACTION
            if (enemyActor.getActionModifierType() != CombatActionType.ACTOR_BLOCKED_BY_STATE) {
                CombatActionDomain action;
                if (enemyActor.getActionModifierType() == CombatActionType.SKILL_REPEAT_BY_STATE) {

                    action = skillsManager.executeSkill(
                            context,
                            CombatActorType.ENEMY,
                            enemyActor.getLastSkillUsed()
                    );

                } else {
                    action = skillsManager.executeSkill(
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
        if(context.getUser().getStats().getCurrentHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
        }
        if(context.getEnemy().getStats().getCurrentHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_WON);
        }

        return buildCombatTurnResult(context, combat);
    }

    //------------------------------------------------------------------------------------------------------------------

    private CombatTurnResultDomain buildCombatTurnResult(CombatContext context, CombatDomain combat)
    {
        return CombatTurnResultDomain.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(context.getActions())
                .combatStatus(combat.getCombatStatus())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDomain buildTimeoutCombatTurnResult(CombatDomain combat) {

        List<CombatActionDomain> actions = new ArrayList<>();

        CombatActionDomain action =
                CombatActionDomain.builder()
                        .actor(CombatActorType.USER)
                        .actionType(CombatActionType.TIMEOUT)
                        .build();

        actions.add(action);

        return CombatTurnResultDomain.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(actions)
                .combatStatus(combat.getCombatStatus())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDomain buildAbandonCombatTurnResult(CombatDomain combat) {

        List<CombatActionDomain> actions = new ArrayList<>();

        CombatActionDomain action =
                CombatActionDomain.builder()
                        .actor(CombatActorType.USER)
                        .actionType(CombatActionType.ABANDON)
                        .build();

        actions.add(action);

        return CombatTurnResultDomain.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(actions)
                .combatStatus(combat.getCombatStatus())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}