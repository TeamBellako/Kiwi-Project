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

        // user.turns > 0 at start = mid-round bonus action (the round started in a previous call).
        // States already ticked then; do NOT tick again.
        boolean isMidRoundBonus = userActor.getStats().getStat(StatType.TURNS) > 0;

        if (!isMidRoundBonus) {
            //APPLY CURRENT STATES
            statusManager.applyActiveStatesEffectsToActor(userActor, combat);

            //CHECK USER LIFE
            if (userActor.getStats().getCurrentHp() <= 0) {
                combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
                return buildCombatTurnResult(combat);
            }
        }

        //EXECUTE ACTION (skipped if user is in TURNS debt)
        if (userActor.getStats().getStat(StatType.TURNS) < 0) {
            userActor.getStats().setStat(
                    StatType.TURNS,
                    userActor.getStats().getStat(StatType.TURNS) + 1
            );
            combat.addAction(CombatActionDomain.builder()
                    .actor(CombatActorType.USER)
                    .actionType(CombatActionType.ACTOR_SKIPPED_BY_TURNS)
                    .build());
        } else if (userActor.getActionModifierType() != CombatActionType.ACTOR_BLOCKED_BY_STATE) {
            CombatActionDomain action;
            if (userActor.getActionModifierType() == CombatActionType.SKILL_REPEAT_BY_STATE) {

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

        // Consume one bonus action if we were in mid-round bonus mode.
        if (isMidRoundBonus) {
            userActor.getStats().setStat(
                    StatType.TURNS,
                    userActor.getStats().getStat(StatType.TURNS) - 1
            );
        }

        // If user still has positive TURNS, more bonus actions are pending.
        // Return early without enemy phase, without state-tick, without turnNumber++.
        if (userActor.getStats().getStat(StatType.TURNS) > 0) {
            return buildCombatTurnResult(combat, true);
        }

        //REDUCE STATES TURNS (user)
        statusManager.reduceStatesTurnsToActor(userActor, combat);

        // ENEMY TURN
        if(combat.getEnemy().getStats().getStat(StatType.CURRENT_HP) > 0
                && combat.getUser().getStats().getStat(StatType.CURRENT_HP) > 0) {

            CombatActorDomain enemyActor = combat.getActor(CombatActorType.ENEMY);

            //APPLY CURRENT STATES
            statusManager.applyActiveStatesEffectsToActor(enemyActor, combat);

            //CHECK ENEMY LIFE
            if(enemyActor.getStats().getCurrentHp() <= 0) {
                combat.setCombatStatus(CombatGeneralStatus.USER_WON);
                return buildCombatTurnResult(combat);
            }

            // ENEMY TURNS counter drives action count this round.
            // > 0: extra actions (drain by 1, run 2 actions).
            // < 0: skip (drain by 1 toward 0, run 0 actions and emit ACTOR_SKIPPED_BY_TURNS).
            int enemyActions = 1;

            if (enemyActor.getStats().getStat(StatType.TURNS) > 0) {
                enemyActor.getStats().setStat(
                        StatType.TURNS,
                        enemyActor.getStats().getStat(StatType.TURNS) - 1
                );
                enemyActions += 1;
            }
            if (enemyActor.getStats().getStat(StatType.TURNS) < 0) {
                enemyActor.getStats().setStat(
                        StatType.TURNS,
                        enemyActor.getStats().getStat(StatType.TURNS) + 1
                );
                enemyActions -= 1;
            }
            enemyActions = Math.max(0, enemyActions);

            if (enemyActions == 0) {
                combat.addAction(CombatActionDomain.builder()
                        .actor(CombatActorType.ENEMY)
                        .actionType(CombatActionType.ACTOR_SKIPPED_BY_TURNS)
                        .build());
            }

            //EXECUTE ACTIONS
            for (int i = 0; i < enemyActions; i++) {
                if (enemyActor.getActionModifierType() == CombatActionType.ACTOR_BLOCKED_BY_STATE) {
                    break;
                }
                CombatActionDomain action;
                if (enemyActor.getActionModifierType() == CombatActionType.SKILL_REPEAT_BY_STATE) {

                    action = skillsManager.executeSkill(
                            combat,
                            CombatActorType.ENEMY,
                            enemyActor.getLastSkillUsed()
                    );

                } else {
                    Long enemySkill = enemyAI.chooseSkill(combat);
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
        return buildCombatTurnResult(combat, false);
    }

    private CombatTurnResultDomain buildCombatTurnResult(CombatDomain combat, boolean bonusActionPending)
    {
        return CombatTurnResultDomain.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .actions(combat.getActions())
                .combatStatus(combat.getCombatStatus())
                .bonusActionPending(bonusActionPending)
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