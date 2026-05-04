package com.kiwi.combat;

import com.kiwi.features.combat.controllers.CombatStatesService;
import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.StatModificationType;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.combat.engine.CombatContext;
import com.kiwi.features.combat.engine.CombatStatusManager;
import com.kiwi.features.combat.engine.SkillsManager;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.kiwi.combat.EngineTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SkillsManagerTests {

    private ScriptedRandom random;
    private CombatStatesService stateService;
    private CombatStatusManager statusManager;
    private SkillsManager skillsManager;

    @Before
    public void setup() {
        random = new ScriptedRandom();
        stateService = mock(CombatStatesService.class);
        statusManager = mock(CombatStatusManager.class);
        when(statusManager.calculateStateMultiplier(any(), any())).thenReturn(1f);

        skillsManager = new SkillsManager(stateService, statusManager, random);
    }

    private CombatContext newContext(CombatActorDomain user, CombatActorDomain enemy) {
        return new CombatContext(combat(user.getStats().getCurrentHp(), enemy.getStats().getCurrentHp()), user, enemy);
    }

    // ============================================================================================
    // EXECUTE SKILL — DISPATCH
    // ============================================================================================

    @Test
    public void executeSkill_returnsSkipWhenSkillIdIsMinusOne() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, -1L);

        assertEquals(CombatActionType.SKIP, action.getActionType());
        assertEquals(CombatActorType.USER, action.getActor());
    }

    @Test(expected = IllegalStateException.class)
    public void executeSkill_throwsWhenSkillNotInActorMap() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 999L);
    }

    @Test
    public void executeSkill_setsLastSkillUsed() {
        SkillCombatDomain s = skill(7L, "Strike");

        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(7L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 7L);

        assertEquals(Long.valueOf(7L), user.getLastSkillUsed());
    }

    // ============================================================================================
    // APPLY DAMAGE
    // ============================================================================================

    @Test
    public void applyDamage_dealsExpectedAmountOnCleanHit() {
        // 100 patk / 10 mdef * power 2 * variance 1.0 * crit 1 * element 1 * state 1 = 20
        random.queueInts(0, 119)   // hit roll, crit roll (no crit because 119 < 0 is false)
              .queueFloats(0.5f);  // variance midpoint => 1.0

        SkillCombatDomain s = skill(1L, "Strike", damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.DAMAGE, result.getTypeResult());
        assertEquals(20f, result.getValue(), 0.001f);
        assertFalse(result.isCritic());
        // 100 - 20 = 80 hp left
        assertEquals(80, enemy.getStats().getCurrentHp());
    }

    @Test
    public void applyDamage_returnsMissWhenRollExceedsHitChance() {
        // hitChance = acc(0) - eva(50) + effect.hitChance(0) = -50; any roll > -50 misses
        random.queueInts(0);

        SkillCombatDomain s = skill(1L, "Strike", damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 0, 0, 10, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, stats(100, 0, 0, 0, 10, 0, 50, 0));

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.MISS, result.getTypeResult());
        assertEquals(100, enemy.getStats().getCurrentHp());
    }

    @Test
    public void applyDamage_doublesValueOnCriticalHit() {
        // crit triggers when nextInt(120) < lck. lck=100, roll=0 => crit
        random.queueInts(0, 0)
              .queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Strike", damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 0, 0, 10, 100, 0, 100), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertTrue(result.isCritic());
        assertEquals(40f, result.getValue(), 0.001f); // 20 * 2
    }

    @Test
    public void applyDamage_appliesElementMultiplier() {
        random.queueInts(0, 119) // hit, no crit
              .queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Strike", damageEffect(2.0f, AttackType.PHYSICAL, 0, 5L));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        enemy.getElementMultipliers().put(5L,
                ElementMultiplierDomain.builder().elementId(5L).multiplier(2f).build());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(40f, result.getValue(), 0.001f); // 20 * 2 (element)
    }

    @Test
    public void applyDamage_absorbsWhenElementMultiplierIsMinusOne() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Strike", damageEffect(2.0f, AttackType.PHYSICAL, 0, 5L));
        CombatActorDomain user = actor(CombatActorType.USER, stats(50, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        user.getStats().setMaxHp(100);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        enemy.getElementMultipliers().put(5L,
                ElementMultiplierDomain.builder().elementId(5L).multiplier(-1f).build());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.HEAL, result.getTypeResult());
        assertEquals(CombatActorType.USER, result.getTarget());
        // victim is left untouched (the absorb branch routes the result to the attacker)
        assertEquals(100, enemy.getStats().getCurrentHp());
    }

    @Test
    public void applyDamage_appliesStateMultiplier() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        when(statusManager.calculateStateMultiplier(any(), any())).thenReturn(1.5f);

        SkillCombatDomain s = skill(1L, "Strike", damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(30f, result.getValue(), 0.001f); // 20 * 1.5 = 30
    }

    @Test
    public void applyDamage_magicalSkillUsesMatkAndMdef() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        // matk=200, victim mdef=10 (pdef=999 to prove pdef is ignored for magical), power=1
        SkillCombatDomain s = skill(1L, "Spell", damageEffect(1.0f, AttackType.MAGICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 0, 200, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, stats(100, 0, 0, 999, 10, 0, 0, 0));

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(20f, result.getValue(), 0.001f); // 200/10 * 1 * 1 = 20
    }

    @Test
    public void applyDamage_physicalSkillUsesPatkAndPdef() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        // patk=200, victim pdef=10 (mdef=999 to prove mdef is ignored for physical), power=1
        SkillCombatDomain s = skill(1L, "Strike", damageEffect(1.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 200, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, stats(100, 0, 0, 10, 999, 0, 0, 0));

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(20f, result.getValue(), 0.001f); // 200/10 * 1 * 1 = 20
    }

    @Test
    public void applyDamage_treatsZeroDefenseAsOne() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Strike", damageEffect(1.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 50, 0, 0, 0, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, stats(100, 0, 0, 0, 0, 0, 0, 0));

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        // Math.max(0, 1) = 1, so 50 / 1 * 1 = 50
        assertEquals(50f, result.getValue(), 0.001f);
    }

    // ============================================================================================
    // APPLY HEAL
    // ============================================================================================

    @Test
    public void applyHeal_healsBasedOnMaxHpAndPower() {
        SkillCombatDomain s = skill(1L, "Mend", healEffect(0.3f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(40, 100, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        user.getStats().setMaxHp(100);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.HEAL, result.getTypeResult());
        assertEquals(30f, result.getValue(), 0.001f);
        assertEquals(70, user.getStats().getCurrentHp());
    }

    @Test
    public void applyHeal_capsAtMaxHp() {
        SkillCombatDomain s = skill(1L, "Mend", healEffect(1.0f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(80, 100, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        user.getStats().setMaxHp(100);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        assertEquals(100, user.getStats().getCurrentHp());
    }

    // ============================================================================================
    // APPLY MODIFY STAT
    // ============================================================================================

    @Test
    public void applyModifyStat_sumIncreasesStat() {
        SkillCombatDomain s = skill(1L, "Buff",
                modifyStatEffect(StatType.PATK, StatModificationType.SUM, 20f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 50, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        assertEquals(70, user.getStats().getPatk());
    }

    @Test
    public void applyModifyStat_subDecreasesStat() {
        SkillCombatDomain s = skill(1L, "Debuff",
                modifyStatEffect(StatType.MATK, StatModificationType.SUB, 30f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 0, 100, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        assertEquals(70, user.getStats().getMatk());
    }

    @Test
    public void applyModifyStat_mulMultipliesStat() {
        SkillCombatDomain s = skill(1L, "DoubleUp",
                modifyStatEffect(StatType.PDEF, StatModificationType.MUL, 2f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 0, 0, 25, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        assertEquals(50, user.getStats().getPdef());
    }

    @Test
    public void applyModifyStat_divDividesStat() {
        SkillCombatDomain s = skill(1L, "Half",
                modifyStatEffect(StatType.MDEF, StatModificationType.DIV, 2f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 0, 0, 0, 40, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        assertEquals(20, user.getStats().getMdef());
    }

    @Test
    public void applyModifyStat_currentHpFlooredAtZero() {
        SkillCombatDomain s = skill(1L, "Drain",
                modifyStatEffect(StatType.CURRENT_HP, StatModificationType.SUB, 999f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 0, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        assertEquals(0, user.getStats().getCurrentHp());
    }

    @Test
    public void applyModifyStat_maxHpFlooredAtOne() {
        SkillCombatDomain s = skill(1L, "Curse",
                modifyStatEffect(StatType.MAX_HP, StatModificationType.SUB, 999f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 0, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        assertEquals(1, user.getStats().getMaxHp());
    }

    // ============================================================================================
    // APPLY STATUS
    // ============================================================================================

    @Test
    public void applyStatus_succeedsWhenTargetHasNoResistance() {
        random.queueFloats(0.5f); // chance = 1 - 0 = 1, 0.5 < 1 => apply

        CombatActiveStatusDomain applied = CombatActiveStatusDomain.builder().stateId(3L).remainingTurns(2).build();
        when(stateService.applyNewState(eq(3L), eq(2), eq(0f), any(), eq(1L), any())).thenReturn(applied);

        SkillCombatDomain s = skill(1L, "Freeze", applyStatusEffect(3L, 2, 0f));
        s = SkillCombatDomain.builder().id(1L).name("Freeze").effects(List.of(applyStatusEffect(3L, 2, 0f))).build();

        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.STATUS_APPLIED, result.getTypeResult());
        assertEquals(CombatActorType.ENEMY, result.getTarget());
        verify(stateService).applyNewState(eq(3L), eq(2), eq(0f), eq(enemy), eq(1L), any());
    }

    @Test
    public void applyStatus_failsWhenResistanceBlocksRoll() {
        // resistance 0.9, chance = 0.1, roll 0.5 > 0.1 => no status
        random.queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Freeze", applyStatusEffect(3L, 2, 0f));
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getStatusResistances().put(3L,
                StatusResistanceDomain.builder().stateId(3L).resistance(0.9f).build());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        assertTrue(action.getSkillEffectsResults().isEmpty());
        verifyNoInteractions(stateService);
    }

    @Test
    public void applyStatus_skipsResistanceCheckWhenSelfTargeted() {
        // SELF target => attacker == target, branch skipped, no random call expected
        CombatActiveStatusDomain applied = CombatActiveStatusDomain.builder().stateId(10L).remainingTurns(3).build();
        when(stateService.applyNewState(eq(10L), eq(3), eq(0f), any(), eq(1L), any())).thenReturn(applied);

        SkillCombatDomain s = SkillCombatDomain.builder()
                .id(1L)
                .name("ATK Up")
                .effects(List.of(
                        com.kiwi.features.skills.data.domain.SkillEffectDomain.builder()
                                .effectType(com.kiwi.features.skills.data.enums.SkillEffectType.APPLY_STATUS)
                                .target(com.kiwi.features.skills.data.enums.SkillEffectTargetType.SELF)
                                .stateId(10L)
                                .statusDuration(3)
                                .power(0f)
                                .build()
                ))
                .build();

        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        // self resistance shouldn't block self-buff
        user.getStatusResistances().put(10L,
                StatusResistanceDomain.builder().stateId(10L).resistance(1f).build());

        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.STATUS_APPLIED, result.getTypeResult());
        assertEquals(CombatActorType.USER, result.getTarget());
        verify(stateService).applyNewState(eq(10L), eq(3), eq(0f), eq(user), eq(1L), any());
    }

    // ============================================================================================
    // EXECUTE SKILL — MULTI-EFFECT
    // ============================================================================================

    @Test
    public void executeSkill_appliesEffectsInTypeOrder() {
        // DAMAGE then HEAL — even though listed HEAL first in the effects list, dispatch order is fixed.
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        SkillCombatDomain s = SkillCombatDomain.builder()
                .id(1L)
                .name("LifeSteal")
                .effects(List.of(
                        healEffect(0.1f),
                        damageEffect(2.0f, AttackType.PHYSICAL, 0)
                ))
                .build();

        CombatActorDomain user = actor(CombatActorType.USER, stats(50, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        user.getStats().setMaxHp(100);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());

        CombatActionDomain action = skillsManager.executeSkill(newContext(user, enemy), CombatActorType.USER, 1L);

        List<SkillEffectResultDomain> results = action.getSkillEffectsResults();
        assertEquals(2, results.size());
        assertEquals(SkillEffectResultType.DAMAGE, results.get(0).getTypeResult());
        assertEquals(SkillEffectResultType.HEAL, results.get(1).getTypeResult());
    }
}
