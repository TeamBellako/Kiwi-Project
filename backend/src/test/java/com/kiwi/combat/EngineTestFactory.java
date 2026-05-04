package com.kiwi.combat;

import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.enums.StatModificationType;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.skills.data.domain.SkillEffectDomain;
import com.kiwi.features.skills.data.enums.SkillEffectTargetType;
import com.kiwi.features.skills.data.enums.SkillEffectType;

import java.util.*;

public class EngineTestFactory {

    // =========================================================================
    // STATS
    // =========================================================================

    public static StatsDomain stats(int hp, int patk, int matk, int pdef, int mdef, int acc, int eva, int lck) {
        return StatsDomain.builder()
                .currentHp(hp)
                .maxHp(hp)
                .patk(patk)
                .matk(matk)
                .pdef(pdef)
                .mdef(mdef)
                .acc(acc)
                .eva(eva)
                .lck(lck)
                .build();
    }

    public static StatsDomain defaultStats() {
        return stats(100, 100, 100, 10, 10, 100, 0, 0);
    }

    // =========================================================================
    // ACTOR
    // =========================================================================

    public static CombatActorDomain actor(CombatActorType type, StatsDomain stats) {
        return actor(type, stats, new HashMap<>());
    }

    public static CombatActorDomain actor(
            CombatActorType type,
            StatsDomain stats,
            Map<Long, SkillCombatDomain> skills
    ) {
        return CombatActorDomain.builder()
                .type(type)
                .stats(stats)
                .elementMultipliers(new HashMap<>())
                .statusResistances(new HashMap<>())
                .activeStatuses(new ArrayList<>())
                .skills(new HashMap<>(skills))
                .build();
    }

    // =========================================================================
    // SKILL EFFECTS
    // =========================================================================

    public static SkillEffectDomain damageEffect(float power, AttackType attackType, int hitChance) {
        return damageEffect(power, attackType, hitChance, null);
    }

    public static SkillEffectDomain damageEffect(float power, AttackType attackType, int hitChance, Long elementId) {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.DAMAGE)
                .target(SkillEffectTargetType.OPPONENT)
                .power(power)
                .attackType(attackType)
                .hitChance(hitChance)
                .elementId(elementId)
                .build();
    }

    public static SkillEffectDomain healEffect(float power) {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.HEAL)
                .target(SkillEffectTargetType.SELF)
                .power(power)
                .build();
    }

    public static SkillEffectDomain modifyStatEffect(StatType stat, StatModificationType mod, float power) {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.MODIFY_STAT)
                .target(SkillEffectTargetType.SELF)
                .statAffected(stat)
                .statModification(mod)
                .power(power)
                .build();
    }

    public static SkillEffectDomain applyStatusEffect(Long stateId, int duration, float power) {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.APPLY_STATUS)
                .target(SkillEffectTargetType.OPPONENT)
                .stateId(stateId)
                .statusDuration(duration)
                .power(power)
                .build();
    }

    // =========================================================================
    // SKILLS
    // =========================================================================

    public static SkillCombatDomain skill(Long id, String name, SkillEffectDomain... effects) {
        return SkillCombatDomain.builder()
                .id(id)
                .name(name)
                .effects(Arrays.asList(effects))
                .build();
    }

    // =========================================================================
    // COMBAT
    // =========================================================================

    public static CombatDomain combat(int userHp, int enemyHp) {
        return CombatDomain.builder()
                .id(1L)
                .userId(1L)
                .enemyId(1L)
                .combatConfigId(1L)
                .userHp(userHp)
                .userMaxHp(userHp)
                .enemyHp(enemyHp)
                .enemyMaxHp(enemyHp)
                .turnNumber(1)
                .combatStatus(CombatGeneralStatus.ONGOING)
                .build();
    }
}
