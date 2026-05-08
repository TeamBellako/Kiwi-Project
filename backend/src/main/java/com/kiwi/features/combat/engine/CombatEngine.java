package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.CombatActionDomain;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.domain.CombatTurnResultDomain;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.enums.StatType;
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
            CombatDomain combat,
            Long userSkillId
    ) {

        // USER TURN
        CombatActorDomain userActor = combat.getActor(CombatActorType.USER);

        //APPLY CURRENT STATES
        statusManager.applyActiveStatesEffectsToActor(userActor, combat);

        //CHECK USER LIFE
        if(userActor.getStats().getCurrentHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
            return buildCombatTurnResult(combat);
        }

        //EXECUTE ACTION
        if (userActor.getActionModifierType() != CombatActionType.ACTOR_BLOCKED_BY_STATE) {
            CombatActionDomain action;
            if ( userActor.getActionModifierType() == CombatActionType.SKILL_REPEAT_BY_STATE) {

                action = skillsManager.executeSkill(
                        combat,
                        CombatActorType.USER,
                        userActor.getLastSkillUsed()
                );

            } else {
                action = skillsManager.executeSkill(
                        combat,
                        CombatActorType.USER,
                        userSkillId
                );

            }
            combat.addAction(action);
        }

        //REDUCE STATES TURNS
        statusManager.reduceStatesTurnsToActor(userActor, combat);

        // ENEMY TURN
        if(combat.getEnemy().getStats().getStat(StatType.CURRENT_HP) > 0) {

            CombatActorDomain enemyActor = combat.getActor(CombatActorType.ENEMY);

            //APPLY CURRENT STATES
            statusManager.applyActiveStatesEffectsToActor(enemyActor, combat);

            //CHECK ENEMY LIFE
            if(enemyActor.getStats().getCurrentHp() <= 0) {
                combat.setCombatStatus(CombatGeneralStatus.USER_WON);
                return buildCombatTurnResult(combat);
            }

            //CHOOSE SKILL
            Long enemySkill = enemyAI.chooseSkill(combat);

            //EXECUTE ACTION
            if (enemyActor.getActionModifierType() != CombatActionType.ACTOR_BLOCKED_BY_STATE) {
                CombatActionDomain action;
                if (enemyActor.getActionModifierType() == CombatActionType.SKILL_REPEAT_BY_STATE) {

                    action = skillsManager.executeSkill(
                            combat,
                            CombatActorType.ENEMY,
                            enemyActor.getLastSkillUsed()
                    );

                } else {
                    action = skillsManager.executeSkill(
                            combat,
                            CombatActorType.ENEMY,
                            enemySkill
                    );

                }
                combat.addAction(action);
            }

            //REDUCE STATES TURNS
            statusManager.reduceStatesTurnsToActor(enemyActor, combat);
        }

        // TURN UPDATE
        combat.setTurnNumber(combat.getTurnNumber() + 1);

        // CHECK COMBAT END
        if(combat.getUser().getStats().getStat(StatType.CURRENT_HP) <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
        }
        if(combat.getEnemy().getStats().getStat(StatType.CURRENT_HP) <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_WON);
        }

        return buildCombatTurnResult(combat);
    }

    //------------------------------------------------------------------------------------------------------------------

    private CombatTurnResultDomain buildCombatTurnResult(CombatDomain combat)
    {
        return CombatTurnResultDomain.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(combat.getActions())
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