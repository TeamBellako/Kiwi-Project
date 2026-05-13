package com.kiwi.combat;

import com.kiwi.features.combat.data.domain.CombatActionDomain;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.domain.CombatTurnResultDomain;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.combat.engine.CombatEngine;
import com.kiwi.features.combat.engine.CombatStatusManager;
import com.kiwi.features.combat.engine.EnemyAI;
import com.kiwi.features.combat.engine.SkillsManager;
import org.junit.Before;
import org.junit.Test;

import static com.kiwi.combat.EngineTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CombatEngineTests {

    private SkillsManager skillsManager;
    private CombatStatusManager statusManager;
    private EnemyAI enemyAI;
    private CombatEngine engine;

    @Before
    public void setup() {
        skillsManager = mock(SkillsManager.class);
        statusManager = mock(CombatStatusManager.class);
        enemyAI = mock(EnemyAI.class);
        engine = new CombatEngine(skillsManager, statusManager, enemyAI);

        when(skillsManager.executeSkill(any(), any(), any()))
                .thenAnswer(inv -> CombatActionDomain.builder()
                        .actor(inv.getArgument(1))
                        .actionType(CombatActionType.SKILL_USED)
                        .build());

        when(enemyAI.chooseSkill(any())).thenReturn(99L);
    }

    // ============================================================================================
    // EXECUTE TURN — HAPPY PATH
    // ============================================================================================

    @Test
    public void executeTurn_happyPath_bothActorsAttack() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        verify(skillsManager).executeSkill(any(), eq(CombatActorType.USER), eq(5L));
        verify(enemyAI).chooseSkill(any());
        verify(skillsManager).executeSkill(any(), eq(CombatActorType.ENEMY), eq(99L));

        assertEquals(2, combat.getTurnNumber()); // incremented from 1
        assertEquals(CombatGeneralStatus.ONGOING, result.getCombatStatus());
        assertEquals(2, result.getActions().size());
    }

    @Test
    public void executeTurn_appliesActiveStatusesAndReducesTurnsForBothActors() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 1L);

        verify(statusManager).applyActiveStatesEffectsToActor(eq(user), any());
        verify(statusManager).applyActiveStatesEffectsToActor(eq(enemy), any());
        verify(statusManager).reduceStatesTurnsToActor(eq(user), any());
        verify(statusManager).reduceStatesTurnsToActor(eq(enemy), any());
    }

    // ============================================================================================
    // ACTION MODIFIERS
    // ============================================================================================

    @Test
    public void executeTurn_userBlockedByState_skipsUserSkill() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        user.setActionModifierType(CombatActionType.ACTOR_BLOCKED_BY_STATE);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 5L);

        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.USER), any());
        verify(skillsManager).executeSkill(any(), eq(CombatActorType.ENEMY), eq(99L));
    }

    @Test
    public void executeTurn_userRepeatSkillByState_usesLastSkillIdInsteadOfRequested() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        user.setActionModifierType(CombatActionType.SKILL_REPEAT_BY_STATE);
        user.setLastSkillUsed(42L);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 5L);

        verify(skillsManager).executeSkill(any(), eq(CombatActorType.USER), eq(42L));
        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.USER), eq(5L));
    }

    @Test
    public void executeTurn_enemyBlockedByState_skipsEnemySkill() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.setActionModifierType(CombatActionType.ACTOR_BLOCKED_BY_STATE);
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 5L);

        verify(skillsManager).executeSkill(any(), eq(CombatActorType.USER), eq(5L));
        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.ENEMY), any());
    }

    @Test
    public void executeTurn_enemyRepeatSkillByState_usesEnemyLastSkill() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.setActionModifierType(CombatActionType.SKILL_REPEAT_BY_STATE);
        enemy.setLastSkillUsed(77L);
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 5L);

        verify(skillsManager).executeSkill(any(), eq(CombatActorType.ENEMY), eq(77L));
        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.ENEMY), eq(99L));
    }

    // ============================================================================================
    // EARLY EXITS BY HP
    // ============================================================================================

    @Test
    public void executeTurn_userHpZeroAtStart_returnsLostWithoutAttacking() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        user.getStats().setStat(StatType.CURRENT_HP,0);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        assertEquals(CombatGeneralStatus.USER_LOST, result.getCombatStatus());
        verify(skillsManager, never()).executeSkill(any(), any(), any());
        verify(enemyAI, never()).chooseSkill(any());
    }

    @Test
    public void executeTurn_enemyHpZeroAtStart_userActsThenWins() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        // engine checks combat.enemyHp after user turn, but the gate before that uses the actor's current hp
        enemy.getStats().setStat(StatType.CURRENT_HP,0);
        CombatDomain combat = combat(user, enemy);

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        assertEquals(CombatGeneralStatus.USER_WON, result.getCombatStatus());
        verify(enemyAI, never()).chooseSkill(any());
        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.ENEMY), any());
    }

    @Test
    public void executeTurn_userKillsEnemyDuringTurn_setsWonAtEnd() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        // user skill drops enemy hp to 0
        when(skillsManager.executeSkill(any(), eq(CombatActorType.USER), any()))
                .thenAnswer(inv -> {
                    enemy.getStats().setStat(StatType.CURRENT_HP,0);
                    return CombatActionDomain.builder()
                            .actor(CombatActorType.USER)
                            .actionType(CombatActionType.SKILL_USED)
                            .build();
                });

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        assertEquals(CombatGeneralStatus.USER_WON, result.getCombatStatus());
        // enemy turn skipped because currentHp == 0 at the gate
        verify(enemyAI, never()).chooseSkill(any());
    }

    @Test
    public void executeTurn_enemyKillsUserDuringTurn_setsLostAtEnd() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        when(skillsManager.executeSkill(any(), eq(CombatActorType.ENEMY), any()))
                .thenAnswer(inv -> {
                    user.getStats().setStat(StatType.CURRENT_HP,0);
                    return CombatActionDomain.builder()
                            .actor(CombatActorType.ENEMY)
                            .actionType(CombatActionType.SKILL_USED)
                            .build();
                });

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        assertEquals(CombatGeneralStatus.USER_LOST, result.getCombatStatus());
    }

    // ============================================================================================
    // TURN COUNTER
    // ============================================================================================

    @Test
    public void executeTurn_incrementsTurnNumberOnSurvive() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);
        combat.setTurnNumber(5);

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        assertEquals(6, combat.getTurnNumber());
        assertEquals(6, result.getTurnNumber());
    }

    @Test
    public void executeTurn_userKilledByStateBeforeAction_returnsLostEarly() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        // simulate a burn that drops the user from 100 -> 0 during applyActiveStates
        doAnswer(inv -> {
            user.getStats().setStat(StatType.CURRENT_HP,0);
            return null;
        }).when(statusManager).applyActiveStatesEffectsToActor(eq(user), any());

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        assertEquals(CombatGeneralStatus.USER_LOST, result.getCombatStatus());
        verify(skillsManager, never()).executeSkill(any(), any(), any());
        verify(enemyAI, never()).chooseSkill(any());
    }

    @Test
    public void executeTurn_doesNotIncrementOnEarlyUserDeath() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        user.getStats().setStat(StatType.CURRENT_HP,0);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);
        combat.setTurnNumber(5);

        engine.executeTurn(combat, 5L);

        assertEquals(5, combat.getTurnNumber());
    }

    // ============================================================================================
    // BUILD-ONLY HELPERS
    // ============================================================================================

    @Test
    public void buildTimeoutCombatTurnResult_emitsTimeoutAction() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);
        combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
        combat.setTurnNumber(7);

        CombatTurnResultDomain result = engine.buildTimeoutCombatTurnResult(combat);

        assertEquals(CombatGeneralStatus.USER_LOST, result.getCombatStatus());
        assertEquals(7, result.getTurnNumber());
        assertEquals(1, result.getActions().size());
        assertEquals(CombatActionType.TIMEOUT, result.getActions().get(0).getActionType());
        assertEquals(CombatActorType.USER, result.getActions().get(0).getActor());
    }

    // ============================================================================================
    // TURNS — ACTION ECONOMY
    // ============================================================================================

    @Test
    public void executeTurn_userBonusAfterSkill_returnsEarlyWithBonusPending() {
        // User uses an ExtraTurn-like skill that sets user.turns to +1 during execution.
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);
        combat.setTurnNumber(5);

        when(skillsManager.executeSkill(any(), eq(CombatActorType.USER), eq(1002L)))
                .thenAnswer(inv -> {
                    user.getStats().setStat(StatType.TURNS, 1);
                    return CombatActionDomain.builder()
                            .actor(CombatActorType.USER)
                            .actionType(CombatActionType.SKILL_USED)
                            .build();
                });

        CombatTurnResultDomain result = engine.executeTurn(combat, 1002L);

        verify(skillsManager).executeSkill(any(), eq(CombatActorType.USER), eq(1002L));
        // Enemy phase MUST NOT run
        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.ENEMY), any());
        verify(enemyAI, never()).chooseSkill(any());
        // State turns must NOT tick on user (round not over)
        verify(statusManager, never()).reduceStatesTurnsToActor(eq(user), any());
        // turnNumber stays put
        assertEquals(5, combat.getTurnNumber());
        // Counter preserved for the next call
        assertEquals(1, user.getStats().getStat(StatType.TURNS));
        // Flag set so mobile knows to ask for another skill
        assertTrue(result.isBonusActionPending());
    }

    @Test
    public void executeTurn_midRoundBonus_skipsApplyStatesAndConsumesOneBonus() {
        // Second call: user.turns starts at +1 (a bonus action is pending).
        // applyActiveStates must NOT run on user, and the counter must drain by 1.
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        user.getStats().setStat(StatType.TURNS, 1);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);
        combat.setTurnNumber(5);

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        verify(skillsManager).executeSkill(any(), eq(CombatActorType.USER), eq(5L));
        verify(statusManager, never()).applyActiveStatesEffectsToActor(eq(user), any());
        // After consuming the bonus, counter is 0; enemy phase runs normally.
        verify(skillsManager).executeSkill(any(), eq(CombatActorType.ENEMY), eq(99L));
        assertEquals(0, user.getStats().getStat(StatType.TURNS));
        assertEquals(6, combat.getTurnNumber());
        assertFalse(result.isBonusActionPending());
    }

    @Test
    public void executeTurn_userBonusOfTwo_chainsThreeCallsBeforeEnemyActs() {
        // First call sets turns=+2 (two bonus actions queued); second & third are bonus calls;
        // enemy only acts on the third call after all bonuses are consumed.
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        when(skillsManager.executeSkill(any(), eq(CombatActorType.USER), eq(1002L)))
                .thenAnswer(inv -> {
                    user.getStats().setStat(StatType.TURNS, 2);
                    return CombatActionDomain.builder()
                            .actor(CombatActorType.USER)
                            .actionType(CombatActionType.SKILL_USED)
                            .build();
                });

        CombatTurnResultDomain r1 = engine.executeTurn(combat, 1002L);
        assertTrue(r1.isBonusActionPending());
        assertEquals(2, user.getStats().getStat(StatType.TURNS));

        CombatTurnResultDomain r2 = engine.executeTurn(combat, 5L);
        assertTrue(r2.isBonusActionPending());
        assertEquals(1, user.getStats().getStat(StatType.TURNS));
        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.ENEMY), any());

        CombatTurnResultDomain r3 = engine.executeTurn(combat, 5L);
        assertFalse(r3.isBonusActionPending());
        assertEquals(0, user.getStats().getStat(StatType.TURNS));
        verify(skillsManager).executeSkill(any(), eq(CombatActorType.ENEMY), eq(99L));
    }

    @Test
    public void executeTurn_enemyDebt_runsEnemyPhaseButSkipsSkillAndEmitsAction() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getStats().setStat(StatType.TURNS, -1);
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 5L);

        verify(skillsManager).executeSkill(any(), eq(CombatActorType.USER), eq(5L));
        // Enemy phase plumbing runs (states tick) but no skill is executed.
        verify(statusManager).applyActiveStatesEffectsToActor(eq(enemy), any());
        verify(statusManager).reduceStatesTurnsToActor(eq(enemy), any());
        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.ENEMY), any());
        verify(enemyAI, never()).chooseSkill(any());
        assertEquals(0, enemy.getStats().getStat(StatType.TURNS));
        // The skip event is in the action log for the mobile to render.
        assertTrue(combat.getActions().stream().anyMatch(a ->
                a.getActor() == CombatActorType.ENEMY
                        && a.getActionType() == CombatActionType.ACTOR_SKIPPED_BY_TURNS));
    }

    @Test
    public void executeTurn_userDebt_skipsUserAndEnemyActs() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        user.getStats().setStat(StatType.TURNS, -1);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 5L);

        verify(skillsManager, never()).executeSkill(any(), eq(CombatActorType.USER), any());
        verify(skillsManager).executeSkill(any(), eq(CombatActorType.ENEMY), eq(99L));
        assertEquals(0, user.getStats().getStat(StatType.TURNS));
        assertTrue(combat.getActions().stream().anyMatch(a ->
                a.getActor() == CombatActorType.USER
                        && a.getActionType() == CombatActionType.ACTOR_SKIPPED_BY_TURNS));
    }

    @Test
    public void executeTurn_enemyBonusRunsExtraAction() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getStats().setStat(StatType.TURNS, 1);
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 5L);

        verify(skillsManager).executeSkill(any(), eq(CombatActorType.USER), eq(5L));
        verify(skillsManager, times(2)).executeSkill(any(), eq(CombatActorType.ENEMY), eq(99L));
        verify(enemyAI, times(2)).chooseSkill(any());
        assertEquals(0, enemy.getStats().getStat(StatType.TURNS));
    }

    @Test
    public void executeTurn_extraEnemyActionsStillTickStatesOnce() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getStats().setStat(StatType.TURNS, 1);
        CombatDomain combat = combat(user, enemy);

        engine.executeTurn(combat, 5L);

        verify(skillsManager, times(2)).executeSkill(any(), eq(CombatActorType.ENEMY), any());
        verify(statusManager).applyActiveStatesEffectsToActor(eq(enemy), any());
        verify(statusManager).reduceStatesTurnsToActor(eq(enemy), any());
    }

    @Test
    public void executeTurn_normalRoundWithZeroTurns_actsAsBefore() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatTurnResultDomain result = engine.executeTurn(combat, 5L);

        assertFalse(result.isBonusActionPending());
        verify(skillsManager).executeSkill(any(), eq(CombatActorType.USER), eq(5L));
        verify(skillsManager).executeSkill(any(), eq(CombatActorType.ENEMY), eq(99L));
    }

    @Test
    public void buildAbandonCombatTurnResult_emitsAbandonAction() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);
        combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
        combat.setTurnNumber(3);

        CombatTurnResultDomain result = engine.buildAbandonCombatTurnResult(combat);

        assertEquals(CombatGeneralStatus.USER_LOST, result.getCombatStatus());
        assertEquals(3, result.getTurnNumber());
        assertEquals(1, result.getActions().size());
        assertEquals(CombatActionType.ABANDON, result.getActions().get(0).getActionType());
    }
}
