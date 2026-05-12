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

    public static StatsDomain stats(int hp, int maxHp, int shield, int patk, int matk, int pdef, int mdef, int acc, int eva, int lck) {
        return StatsDomain.builder()
                .currentHp(hp)
                .maxHp(maxHp)
                .shield(shield)
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
        return stats(100, 100,25, 100, 100, 10, 10, 100, 0, 0);
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
                .blockedSkills(new ArrayList<>())
                .resetCooldownSkills(new ArrayList<>())
                .build();
    }

    // =========================================================================
    // SKILL EFFECTS
    // =========================================================================

    public static SkillEffectDomain damageEffect(float power, AttackType attackType, int hitChance) {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.DAMAGE)
                .target(SkillEffectTargetType.OPPONENT)
                .power(power)
                .attackType(attackType)
                .hitChance(hitChance)
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

    public static SkillEffectDomain consumeStatusEffect(int turns) {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.CONSUME_STATUS)
                .target(SkillEffectTargetType.OPPONENT)
                .turns(turns)
                .build();
    }

    public static SkillEffectDomain extendBuffsEffect(int turns) {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.EXTEND_BUFFS)
                .target(SkillEffectTargetType.SELF)
                .turns(turns)
                .build();
    }

    public static SkillEffectDomain copyBuffsEffect() {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.COPY_BUFFS)
                .target(SkillEffectTargetType.OPPONENT)
                .build();
    }

    public static SkillEffectDomain swapBuffsEffect() {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.SWAP_BUFFS)
                .target(SkillEffectTargetType.OPPONENT)
                .build();
    }

    public static SkillEffectDomain resetCooldownsEffect() {
        return SkillEffectDomain.builder()
                .effectType(SkillEffectType.RESET_COOLDOWNS)
                .target(SkillEffectTargetType.SELF)
                .build();
    }

    public static CombatActiveStatusDomain activeStatus(Long stateId, int remainingTurns, Float value, StatType statAffected) {
        return CombatActiveStatusDomain.builder()
                .stateId(stateId)
                .remainingTurns(remainingTurns)
                .value(value)
                .statAffected(statAffected)
                .build();
    }

    // =========================================================================
    // SKILLS
    // =========================================================================

    public static SkillCombatDomain skill(Long id, String name, Long elementId, SkillEffectDomain... effects) {
        return SkillCombatDomain.builder()
                .id(id)
                .name(name)
                .elementId(elementId)
                .effects(Arrays.asList(effects))
                .build();
    }

    // =========================================================================
    // COMBAT
    // =========================================================================

    public static CombatDomain combat(CombatActorDomain user, CombatActorDomain enemy) {
        return new CombatDomain(1L, 1L, user, enemy, 1, CombatGeneralStatus.ONGOING, null);
    }
}
