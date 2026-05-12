package com.kiwi.combat;

import com.kiwi.features.combat.controllers.CombatStatesService;
import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.StatModificationType;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import com.kiwi.features.combat.engine.CombatStatusManager;
import com.kiwi.features.combat.engine.SkillsManager;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.skills.data.domain.SkillEffectResultDomain;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.kiwi.combat.EngineTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;

public class SkillsManagerTests {

    private ScriptedRandom random;
    private SkillsManager skillsManager;

    private final CombatStatesTestRepositoryInMemory combatStatesRepo =
            new CombatStatesTestRepositoryInMemory();

    private final CombatActiveStatusesTestRepositoryInMemory combatActiveStatusesRepo =
            new CombatActiveStatusesTestRepositoryInMemory();



    @Before
    public void setup() {
        random = new ScriptedRandom();
        CombatStatesService stateService = new CombatStatesService(combatStatesRepo, combatActiveStatusesRepo);
        CombatStatusManager statusManager = new CombatStatusManager(random);
        skillsManager = new SkillsManager(stateService, statusManager, random);
    }

    // ============================================================================================
    // EXECUTE SKILL — DISPATCH
    // ============================================================================================

    @Test
    public void executeSkill_returnsSkipWhenSkillIdIsMinusOne() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, -1L);

        assertEquals(CombatActionType.SKIP, action.getActionType());
        assertEquals(CombatActorType.USER, action.getActor());
    }

    @Test(expected = IllegalStateException.class)
    public void executeSkill_throwsWhenSkillNotInActorMap() {
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats());
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 999L);
    }

    @Test
    public void executeSkill_setsLastSkillUsed() {
        SkillCombatDomain s = skill(7L, "Strike", 1L);

        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(7L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 7L);

        assertEquals(Long.valueOf(7L), user.getLastSkillUsed());
    }

    // ============================================================================================
    // APPLY DAMAGE
    // ============================================================================================

    @Test
    public void applyDamage_dealsExpectedAmountOnCleanHit() {  // NO SHIELD
        // 100 patk / 10 mdef * power 2 * variance 1.0 * crit 1 * element 1 * state 1 = 20
        random.queueInts(0, 119)   // hit roll, crit roll (no crit because 119 < 0 is false)
              .queueFloats(0.5f);  // variance midpoint => 1.0

        SkillCombatDomain s = skill(1L, "Strike", 5L, damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getStats().setStat(StatType.SHIELD, 0);
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

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

        SkillCombatDomain s = skill(1L, "Strike", 1L, damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25, 100, 0, 0, 10, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, stats(100, 100, 25,0, 0, 0, 10, 0, 50, 0));
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.MISS_DAMAGE, result.getTypeResult());
        assertEquals(100, enemy.getStats().getCurrentHp());
    }

    @Test
    public void applyDamage_doublesValueOnCriticalHit() {
        // crit triggers when nextInt(120) < lck. lck=100, roll=0 => crit
        random.queueInts(0, 0)
              .queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Strike", 1L, damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25, 100, 0, 0, 10, 100, 0, 100), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertTrue(result.isCritic());
        assertEquals(40f, result.getValue(), 0.001f); // 20 * 2
    }

    @Test
    public void applyDamage_appliesElementMultiplier() {
        random.queueInts(0, 119) // hit, no crit
              .queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Strike", 5L, damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        enemy.getElementMultipliers().put(5L,
                ElementMultiplierDomain.builder().elementId(5L).multiplier(2f).build());

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(40f, result.getValue(), 0.001f); // 20 * 2 (element)
    }

    @Test
    public void applyDamage_absorbsWhenElementMultiplierIsMinusOne() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Strike", 5L, damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(50, 100, 25, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        enemy.getElementMultipliers().put(5L,
                ElementMultiplierDomain.builder().elementId(5L).multiplier(-1f).build());

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

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

        SkillCombatDomain s = skill(1L, "Strike", 5L, damageEffect(2.0f, AttackType.PHYSICAL, 0));
        CombatActiveStatusDomain stat_upPATK =  CombatActiveStatusDomain.builder().stateId(13L).statAffected(StatType.PATK).value(1.5f).build();
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        user.getActiveStatuses().add(stat_upPATK);

        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(30f, result.getValue(), 0.001f); // 20 * 1.5 = 30
    }

    @Test
    public void applyDamage_magicalSkillUsesMatkAndMdef() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        // matk=200, victim mdef=10 (pdef=999 to prove pdef is ignored for magical), power=1
        SkillCombatDomain s = skill(1L, "Spell", 1L, damageEffect(1.0f, AttackType.MAGICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,0, 200, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, stats(100, 100, 25,0, 0, 999, 10, 0, 0, 0));
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(20f, result.getValue(), 0.001f); // 200/10 * 1 * 1 = 20
    }

    @Test
    public void applyDamage_physicalSkillUsesPatkAndPdef() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        // patk=200, victim pdef=10 (mdef=999 to prove mdef is ignored for physical), power=1
        SkillCombatDomain s = skill(1L, "Strike", 1L, damageEffect(1.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,200, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, stats(100, 100, 25,0, 0, 10, 999, 0, 0, 0));
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(20f, result.getValue(), 0.001f); // 200/10 * 1 * 1 = 20
    }

    @Test
    public void applyDamage_treatsZeroDefenseAsOne() {
        random.queueInts(0, 119)
              .queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Strike", 1L, damageEffect(1.0f, AttackType.PHYSICAL, 0));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,50, 0, 0, 0, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, stats(100, 100, 25,0, 0, 0, 0, 0, 0, 0));
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        // Math.max(0, 1) = 1, so 50 / 1 * 1 = 50
        assertEquals(50f, result.getValue(), 0.001f);
    }

    // ============================================================================================
    // APPLY HEAL
    // ============================================================================================

    @Test
    public void applyHeal_healsBasedOnMaxHpAndPower() {
        SkillCombatDomain s = skill(1L, "Mend", 1L, healEffect(0.3f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(40, 100, 25,100, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.HEAL, result.getTypeResult());
        assertEquals(30f, result.getValue(), 0.001f);
        assertEquals(70, user.getStats().getCurrentHp());
    }

    @Test
    public void applyHeal_capsAtMaxHp() {
        SkillCombatDomain s = skill(1L, "Mend", 1L, healEffect(1.0f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(80, 100, 25,100, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertEquals(100, user.getStats().getCurrentHp());
    }

    // ============================================================================================
    // APPLY MODIFY STAT
    // ============================================================================================

    @Test
    public void applyModifyStat_sumIncreasesStat() {
        SkillCombatDomain s = skill(1L, "Buff", 1L,
                modifyStatEffect(StatType.PATK, StatModificationType.SUM, 20f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,50, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertEquals(70, user.getStats().getPatk());
    }

    @Test
    public void applyModifyStat_subDecreasesStat() {
        SkillCombatDomain s = skill(1L, "Debuff", 1L,
                modifyStatEffect(StatType.MATK, StatModificationType.SUB, 30f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,0, 100, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertEquals(70, user.getStats().getMatk());
    }

    @Test
    public void applyModifyStat_mulMultipliesStat() {
        SkillCombatDomain s = skill(1L, "DoubleUp", 1L,
                modifyStatEffect(StatType.PDEF, StatModificationType.MUL, 2f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,0, 0, 25, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertEquals(50, user.getStats().getPdef());
    }

    @Test
    public void applyModifyStat_divDividesStat() {
        SkillCombatDomain s = skill(1L, "Half", 1L,
                modifyStatEffect(StatType.MDEF, StatModificationType.DIV, 2f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,0, 0, 0, 40, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertEquals(20, user.getStats().getMdef());
    }

    @Test
    public void applyModifyStat_currentHpFlooredAtZero() {
        SkillCombatDomain s = skill(1L, "Drain",1L,
                modifyStatEffect(StatType.CURRENT_HP, StatModificationType.SUB, 999f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,0, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertEquals(0, user.getStats().getCurrentHp());
    }

    @Test
    public void applyModifyStat_maxHpFlooredAtOne() {
        SkillCombatDomain s = skill(1L, "Curse",1L,
                modifyStatEffect(StatType.MAX_HP, StatModificationType.SUB, 999f));
        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25,0, 0, 0, 0, 0, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertEquals(1, user.getStats().getMaxHp());
    }

    // ============================================================================================
    // APPLY STATUS
    // ============================================================================================

    @Test
    public void applyStatus_succeedsWhenTargetHasNoResistance() {
        random.queueFloats(0.5f); // chance = 1 - 0 = 1, 0.5 < 1 => apply

        combatStatesRepo.save(new CombatStatePersistence (3L, "Test",1, "Desc"));

        SkillCombatDomain s = skill(1L, "Freeze", 1L, applyStatusEffect(3L, 2, 0f));

        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.STATUS_APPLIED, result.getTypeResult());
        assertEquals(CombatActorType.ENEMY, result.getTarget());
    }

    @Test
    public void applyStatus_failsWhenResistanceBlocksRoll() {
        // resistance 0.9, chance = 0.1, roll 0.5 > 0.1 => no status
        random.queueFloats(0.5f);

        SkillCombatDomain s = skill(1L, "Freeze", 1L, applyStatusEffect(3L, 2, 0f));
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getStatusResistances().put(3L,
                StatusResistanceDomain.builder().stateId(3L).resistance(0.9f).build());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.MISS_STATUS, result.getTypeResult());
    }

    @Test
    public void applyStatus_skipsResistanceCheckWhenSelfTargeted() {
        // SELF target => attacker == target, branch skipped, no random call expected
        combatStatesRepo.save(new CombatStatePersistence (10L, "Test",1, "Desc"));

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
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.STATUS_APPLIED, result.getTypeResult());
        assertEquals(CombatActorType.USER, result.getTarget());
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

        CombatActorDomain user = actor(CombatActorType.USER, stats(50, 100, 25,100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        List<SkillEffectResultDomain> results = action.getSkillEffectsResults();
        assertEquals(2, results.size());
        assertEquals(SkillEffectResultType.DAMAGE, results.get(0).getTypeResult());
        assertEquals(SkillEffectResultType.HEAL, results.get(1).getTypeResult());
    }

    // ============================================================================================
    // APPLY CONSUME STATUS
    // ============================================================================================

    @Test
    public void applyConsumeStatus_decreasesRemainingTurnsWhenPartial() {
        // turns to consume (2) < status remaining (5) => STATUS_CONSUMED, remaining becomes 3
        CombatActiveStatusDomain status = activeStatus(7L, 5, 1f, StatType.PATK);

        SkillCombatDomain s = skill(1L, "Drain", 1L, consumeStatusEffect(2));
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getActiveStatuses().add(status);
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.STATUS_CONSUMED, result.getTypeResult());
        assertEquals(CombatActorType.ENEMY, result.getTarget());
        assertEquals(Integer.valueOf(2), result.getTurns());
        assertEquals(1, enemy.getActiveStatuses().size());
        assertEquals(3, enemy.getActiveStatuses().get(0).getRemainingTurns());
    }

    @Test
    public void applyConsumeStatus_removesStatusWhenTurnsExceedRemaining() {
        // turns to consume (5) >= status remaining (2) => STATUS_REMOVED
        CombatActiveStatusDomain status = activeStatus(7L, 2, 1f, StatType.PATK);

        SkillCombatDomain s = skill(1L, "Purge", 1L, consumeStatusEffect(5));
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getActiveStatuses().add(status);
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.STATUS_REMOVED, result.getTypeResult());
        assertEquals(CombatActorType.ENEMY, result.getTarget());
        assertTrue(enemy.getActiveStatuses().isEmpty());
    }

    @Test
    public void applyConsumeStatus_emitsOneResultPerStatus() {
        // Two statuses on target — both consumed in the same call
        CombatActiveStatusDomain s1 = activeStatus(7L, 5, 1f, StatType.PATK);
        CombatActiveStatusDomain s2 = activeStatus(8L, 1, 1f, StatType.MATK);

        SkillCombatDomain s = skill(1L, "Eat", 1L, consumeStatusEffect(2));
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getActiveStatuses().add(s1);
        enemy.getActiveStatuses().add(s2);
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        List<SkillEffectResultDomain> results = action.getSkillEffectsResults();
        assertEquals(2, results.size());
        assertEquals(SkillEffectResultType.STATUS_CONSUMED, results.get(0).getTypeResult());
        assertEquals(SkillEffectResultType.STATUS_REMOVED, results.get(1).getTypeResult());
        assertEquals(1, enemy.getActiveStatuses().size());
        assertEquals(7L, (long) enemy.getActiveStatuses().get(0).getStateId());
        assertEquals(3, enemy.getActiveStatuses().get(0).getRemainingTurns());
    }

    @Test
    public void applyConsumeStatus_emitsNothingWhenNoStatuses() {
        SkillCombatDomain s = skill(1L, "Eat", 1L, consumeStatusEffect(2));
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertTrue(action.getSkillEffectsResults().isEmpty());
    }

    @Test
    public void applyConsumeStatus_boostsSubsequentDamage() {
        // Multiplier accumulates: starts at 1, += min(effect.turns, remainingTurns) per status.
        // One status with 5 remaining, effect.turns=4 => +4. Final multiplier = 1 + 4 = 5.
        // Base damage with stats (100 patk / 10 pdef * power 1) = 10, then * 5 = 50.
        random.queueInts(0, 119)   // hit, no crit
              .queueFloats(0.5f);  // variance midpoint => 1.0

        CombatActiveStatusDomain status = activeStatus(7L, 5, 1f, StatType.PATK);

        SkillCombatDomain s = SkillCombatDomain.builder()
                .id(1L)
                .name("EatAndStrike")
                .elementId(1L)
                .effects(List.of(
                        consumeStatusEffect(4),
                        damageEffect(1.0f, AttackType.PHYSICAL, 0)
                ))
                .build();

        CombatActorDomain user = actor(CombatActorType.USER, stats(100, 100, 25, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getStats().setStat(StatType.SHIELD, 0);
        enemy.getActiveStatuses().add(status);
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        // dispatch order is CONSUME_STATUS before DAMAGE, so:
        //  - results[0] = STATUS_CONSUMED on enemy
        //  - results[1] = DAMAGE on enemy, value = 50
        List<SkillEffectResultDomain> results = action.getSkillEffectsResults();
        assertEquals(SkillEffectResultType.STATUS_CONSUMED, results.get(0).getTypeResult());
        SkillEffectResultDomain dmg = results.get(1);
        assertEquals(SkillEffectResultType.DAMAGE, dmg.getTypeResult());
        assertEquals(50f, dmg.getValue(), 0.001f);
        // shield=0, so all 50 damage hits HP: 100 - 50 = 50
        assertEquals(50, enemy.getStats().getCurrentHp());
    }

    // ============================================================================================
    // APPLY EXTEND BUFFS
    // ============================================================================================

    @Test
    public void applyExtendBuffs_addsTurnsToEveryActiveStatus() {
        CombatActiveStatusDomain s1 = activeStatus(7L, 2, 1f, StatType.PATK);
        CombatActiveStatusDomain s2 = activeStatus(8L, 5, 1f, StatType.MATK);

        SkillCombatDomain s = skill(1L, "Prolong", 1L, extendBuffsEffect(3));
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        user.getActiveStatuses().add(s1);
        user.getActiveStatuses().add(s2);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        List<SkillEffectResultDomain> results = action.getSkillEffectsResults();
        assertEquals(2, results.size());
        for (SkillEffectResultDomain r : results) {
            assertEquals(SkillEffectResultType.STATUS_EXTENDED, r.getTypeResult());
            assertEquals(CombatActorType.USER, r.getTarget());
            assertEquals(Integer.valueOf(3), r.getTurns());
        }
        assertEquals(5, user.getActiveStatuses().get(0).getRemainingTurns()); // 2 + 3
        assertEquals(8, user.getActiveStatuses().get(1).getRemainingTurns()); // 5 + 3
    }

    @Test
    public void applyExtendBuffs_emitsNothingWhenNoStatuses() {
        SkillCombatDomain s = skill(1L, "Prolong", 1L, extendBuffsEffect(3));
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertTrue(action.getSkillEffectsResults().isEmpty());
    }

    // ============================================================================================
    // APPLY COPY BUFFS
    // ============================================================================================

    @Test
    public void applyCopyBuffs_clearsAttackerAndCopiesFromTarget() {
        CombatActiveStatusDomain attackerExisting = activeStatus(1L, 10, 1f, StatType.PATK);
        CombatActiveStatusDomain targetBuff1 = activeStatus(7L, 3, 1.5f, StatType.PATK);
        CombatActiveStatusDomain targetBuff2 = activeStatus(8L, 2, 2f, StatType.MATK);

        SkillCombatDomain s = skill(1L, "Mirror", 1L, copyBuffsEffect());
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        user.getActiveStatuses().add(attackerExisting);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getActiveStatuses().add(targetBuff1);
        enemy.getActiveStatuses().add(targetBuff2);
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.BUFFS_COPIED, result.getTypeResult());
        assertEquals(CombatActorType.USER, result.getTarget());

        // attacker now has exactly the target's statuses, not its previous one
        assertEquals(2, user.getActiveStatuses().size());
        assertEquals(7L, (long) user.getActiveStatuses().get(0).getStateId());
        assertEquals(8L, (long) user.getActiveStatuses().get(1).getStateId());

        // target list is unchanged
        assertEquals(2, enemy.getActiveStatuses().size());
    }

    @Test
    public void applyCopyBuffs_copiesAreIndependentInstances() {
        // Mutating the copy must not affect the source.
        CombatActiveStatusDomain targetBuff = activeStatus(7L, 3, 1.5f, StatType.PATK);

        SkillCombatDomain s = skill(1L, "Mirror", 1L, copyBuffsEffect());
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getActiveStatuses().add(targetBuff);
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        CombatActiveStatusDomain copied = user.getActiveStatuses().get(0);
        copied.setRemainingTurns(99);

        assertEquals(3, targetBuff.getRemainingTurns());
    }

    // ============================================================================================
    // APPLY SWAP BUFFS
    // ============================================================================================

    @Test
    public void applySwapBuffs_swapsActiveStatusesBetweenActors() {
        CombatActiveStatusDomain userBuff = activeStatus(1L, 4, 1f, StatType.PATK);
        CombatActiveStatusDomain enemyBuff1 = activeStatus(7L, 3, 1.5f, StatType.MATK);
        CombatActiveStatusDomain enemyBuff2 = activeStatus(8L, 2, 2f, StatType.PDEF);

        SkillCombatDomain s = skill(1L, "Steal", 1L, swapBuffsEffect());
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        user.getActiveStatuses().add(userBuff);
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getActiveStatuses().add(enemyBuff1);
        enemy.getActiveStatuses().add(enemyBuff2);
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.BUFFS_SWAPPED, result.getTypeResult());
        // applySwapBuffs reports target.getType() — i.e. the victim
        assertEquals(CombatActorType.ENEMY, result.getTarget());

        assertEquals(2, user.getActiveStatuses().size());
        assertEquals(7L, (long) user.getActiveStatuses().get(0).getStateId());
        assertEquals(8L, (long) user.getActiveStatuses().get(1).getStateId());

        assertEquals(1, enemy.getActiveStatuses().size());
        assertEquals(1L, (long) enemy.getActiveStatuses().get(0).getStateId());
    }

    @Test
    public void applySwapBuffs_handlesEmptyAttackerStatuses() {
        CombatActiveStatusDomain enemyBuff = activeStatus(7L, 3, 1.5f, StatType.MATK);

        SkillCombatDomain s = skill(1L, "Steal", 1L, swapBuffsEffect());
        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getActiveStatuses().add(enemyBuff);
        CombatDomain combat = combat(user, enemy);

        skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        assertEquals(1, user.getActiveStatuses().size());
        assertEquals(7L, (long) user.getActiveStatuses().get(0).getStateId());
        assertTrue(enemy.getActiveStatuses().isEmpty());
    }

    // ============================================================================================
    // APPLY RESET COOLDOWNS
    // ============================================================================================

    @Test
    public void applyResetCooldowns_listsAllAttackerSkillIds() {
        SkillCombatDomain s1 = skill(1L, "Refresh", 1L, resetCooldownsEffect());
        SkillCombatDomain s2 = skill(2L, "Strike", 1L, damageEffect(1f, AttackType.PHYSICAL, 0));
        SkillCombatDomain s3 = skill(3L, "Mend", 1L, healEffect(0.2f));

        CombatActorDomain user = actor(CombatActorType.USER, defaultStats(), Map.of(
                s1.getId(), s1,
                s2.getId(), s2,
                s3.getId(), s3
        ));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        SkillEffectResultDomain result = action.getSkillEffectsResults().get(0);
        assertEquals(SkillEffectResultType.RESET_COOLDOWNS, result.getTypeResult());
        assertEquals(CombatActorType.USER, result.getTarget());

        List<Long> resultIds = result.getResetCooldownSkills();
        assertEquals(3, resultIds.size());
        assertTrue(resultIds.containsAll(List.of(1L, 2L, 3L)));

        // The actor's reset list is also populated (consumed later by CombatTurnService)
        List<Long> actorIds = user.getResetCooldownSkills();
        assertEquals(3, actorIds.size());
        assertTrue(actorIds.containsAll(List.of(1L, 2L, 3L)));
    }

    // ============================================================================================
    // EXECUTE SKILL — NEW DISPATCH ORDER
    // ============================================================================================

    @Test
    public void executeSkill_dispatchOrderRunsResetThenStatusEffectsThenDamage() {
        // Effects listed deliberately out of order; expected dispatch:
        // RESET_COOLDOWNS, APPLY_STATUS, EXTEND_BUFFS, SWAP_BUFFS, COPY_BUFFS, CONSUME_STATUS, DAMAGE, HEAL, MODIFY_STAT
        random.queueInts(0, 119)   // hit, no crit (for damage)
              .queueFloats(0.5f);  // variance for damage

        SkillCombatDomain s = SkillCombatDomain.builder()
                .id(1L)
                .name("Everything")
                .elementId(1L)
                .effects(List.of(
                        damageEffect(1f, AttackType.PHYSICAL, 0),
                        resetCooldownsEffect(),
                        consumeStatusEffect(1),
                        healEffect(0.1f)
                ))
                .build();

        CombatActiveStatusDomain enemyBuff = activeStatus(7L, 5, 1f, StatType.PATK);
        CombatActorDomain user = actor(CombatActorType.USER, stats(50, 100, 25, 100, 0, 0, 10, 100, 0, 0), Map.of(1L, s));
        CombatActorDomain enemy = actor(CombatActorType.ENEMY, defaultStats());
        enemy.getActiveStatuses().add(enemyBuff);
        CombatDomain combat = combat(user, enemy);

        CombatActionDomain action = skillsManager.executeSkill(combat, CombatActorType.USER, 1L);

        List<SkillEffectResultDomain> results = action.getSkillEffectsResults();
        assertEquals(4, results.size());
        assertEquals(SkillEffectResultType.RESET_COOLDOWNS, results.get(0).getTypeResult());
        assertEquals(SkillEffectResultType.STATUS_CONSUMED, results.get(1).getTypeResult());
        assertEquals(SkillEffectResultType.DAMAGE, results.get(2).getTypeResult());
        assertEquals(SkillEffectResultType.HEAL, results.get(3).getTypeResult());
    }
}
