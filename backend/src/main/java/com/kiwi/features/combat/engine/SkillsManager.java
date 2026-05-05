package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.controllers.CombatStatesService;
import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.enums.*;
import com.kiwi.features.skills.data.domain.SkillEffectDomain;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
import com.kiwi.features.skills.data.enums.SkillEffectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class SkillsManager {

    private final CombatStatesService stateService;
    private final CombatStatusManager statusManager;

    private final Random random;

    //------------------------------------------------------------------------------------------------------------------

    public CombatActionDomain executeSkill(
            CombatContext context,
            CombatActorType actorType,
            Long skillId
    ) {
        // No skills available
        if(skillId == -1) {

            return CombatActionDomain.builder()
                    .actionType(CombatActionType.SKIP)
                    .actor(actorType)
                    .build();
        }

        CombatActorDomain attacker = context.getActor(actorType);

        SkillCombatDomain skill = attacker.getSkills().get(skillId);

        if(skill == null) {
            throw new IllegalStateException("Skill does not exist.");
        }

        List<SkillEffectResultDomain> effectsResults = new ArrayList<>();

        attacker.setLastSkillUsed(skillId);

        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.DAMAGE);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.HEAL);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.MODIFY_STAT);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.APPLY_STATUS);

        return CombatActionDomain.builder()
                .actionType(CombatActionType.SKILL_USED)
                .actor(actorType)
                .skillName(skill.getName())
                .skillEffectsResults(effectsResults)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void applyEffects(
            CombatContext context,
            CombatActorDomain attacker,
            SkillCombatDomain skill,
            List<SkillEffectResultDomain> results,
            SkillEffectType type
    ) {
        for (SkillEffectDomain effect : skill.getEffects()) {

            if (effect.getEffectType() != type) continue;

            CombatActorDomain victim = context.getTarget(attacker.getType(), effect.getTarget());

            if (victim.getActiveStatuses().stream()
                    .anyMatch(s -> s.getStateId() == CombatStateTypes.INVINCIBLE.getId())) {

                results.add(SkillEffectResultDomain.builder()
                        .typeResult(SkillEffectResultType.IMMUNE)
                        .target(victim.getType())
                        .build());

                continue;
            }

            switch (type) {
                case DAMAGE -> results.add(
                        applyDamage(context, attacker, victim, effect, skill.getElementId())
                );

                case HEAL -> results.add(
                        applyHeal(victim, effect)
                );

                case MODIFY_STAT -> results.add(
                        applyModifyStat(victim, effect)
                );

                case APPLY_STATUS -> results.add(
                        applyStatus(attacker, victim, effect, skill.getId(), context.getCombat().getId())
                );

            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDomain applyDamage(
            CombatContext context,
            CombatActorDomain attacker,
            CombatActorDomain victim,
            SkillEffectDomain effect,
            Long skillElementId
    ) {

        // ACCURACY CHECK
        boolean hasPrecision = attacker.getActiveStatuses().stream()
                .anyMatch(s -> s.getStateId() == CombatStateTypes.PRECISION.getId());

        int hitChance;

        if (hasPrecision) {
            hitChance = 100;
        } else {
            hitChance = statusManager.getEffectiveStat(attacker, StatType.ACC)
                    - statusManager.getEffectiveStat(victim, StatType.EVA)
                    + effect.getHitChance();
        }

        int roll = random.nextInt(100);

        if (roll > hitChance) {
            return SkillEffectResultDomain.builder()
                    .typeResult(SkillEffectResultType.MISS_DAMAGE)
                    .target(victim.getType())
                    .build();
        }

        // ATK / DEF
        float attackerAtk =
                effect.getAttackType() == AttackType.PHYSICAL
                        ? statusManager.getEffectiveStat(attacker, StatType.PATK)
                        : statusManager.getEffectiveStat(attacker, StatType.MATK);

        float victimDef =
                effect.getAttackType() == AttackType.MAGICAL
                        ? statusManager.getEffectiveStat(victim, StatType.PDEF)
                        : statusManager.getEffectiveStat(victim, StatType.MDEF);

        float baseDamage =
                (attackerAtk / Math.max(victimDef, 1)) * effect.getPower();

        // CRIT
        boolean crit =
                random.nextInt(120) < statusManager.getEffectiveStat(attacker, StatType.LCK);

        float critMultiplier = crit ? 2f : 1f;

        // RANDOM VARIANCE
        float variance =
                0.9f + random.nextFloat() * 0.2f;

        // ELEMENT
        ElementMultiplierDomain elementMultiplier =
                victim.getElementMultipliers()
                        .getOrDefault(skillElementId, null);

        float elementMultiplierValue = (elementMultiplier != null)
                ? elementMultiplier.getMultiplier()
                : 1f;

        // STATE MULTIPLIER
        float stateMultiplier = attackerAtk* victimDef;


        float modifiers =
                variance
                        * critMultiplier
                        * elementMultiplierValue
                        * stateMultiplier;

        int damage = Math.round(baseDamage * modifiers);

        // ABSORB
        if (elementMultiplierValue == -1f) {

            attacker.heal(damage);

            return SkillEffectResultDomain.builder()
                    .typeResult(SkillEffectResultType.HEAL)
                    .target(attacker.getType())
                    .value((float)damage)
                    .critic(false)
                    .build();
        }

        int realDamage = victim.damage(damage, true);

        // BOND STATE
        CombatActiveStatusDomain bondState = victim.getActiveStatuses().stream()
                .filter(s -> s.getStateId() == CombatStateTypes.BOND.getId())
                .findFirst()
                .orElse(null);

        if (bondState != null) {

            float stateValue = bondState.getValue();

            CombatActorDomain other =
                    (victim == context.getUser())
                            ? context.getEnemy()
                            : context.getUser();

            int reflected = Math.round(realDamage * stateValue);

            other.damage(reflected, true);

            CombatActionDomain bondAction =
                    CombatActionDomain.builder()
                            .actor(other.getType())
                            .actionType(CombatActionType.ACTOR_DAMAGED_BY_STATE)
                            .state(bondState)
                            .stateEffectValue((float) reflected)
                            .build();

            context.addAction(bondAction);
        }

        return SkillEffectResultDomain.builder()
                .typeResult(SkillEffectResultType.DAMAGE)
                .target(victim.getType())
                .value((float)damage)
                .critic(crit)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDomain applyHeal(
            CombatActorDomain target,
            SkillEffectDomain effect
    ) {

        int heal = Math.round(target.getStats().getStat(StatType.MAX_HP) * effect.getPower());

        target.heal(heal);

        return SkillEffectResultDomain.builder()
                .typeResult(SkillEffectResultType.HEAL)
                .target(target.getType())
                .value((float)heal)
                .critic(false)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDomain applyModifyStat(
            CombatActorDomain target,
            SkillEffectDomain effect
    ) {

        // CHECK BUFF_BLOCK
        boolean hasBuffBlock = target.getActiveStatuses().stream()
                .anyMatch(s -> s.getStateId() == CombatStateTypes.BUFF_BLOCK.getId());

        if (hasBuffBlock
                && (effect.getStatModification() == StatModificationType.MUL
                || effect.getStatModification() == StatModificationType.SUM)
                && effect.getPower() > 1f) {

            return null;
        }

        StatType stat = effect.getStatAffected();

        int baseValue = target.getStats().getStat(stat);
        StatModificationType modificationType = effect.getStatModification();

        // CHECK REVERSION
        boolean hasReversion = target.getActiveStatuses().stream()
                .anyMatch(s -> s.getStateId() == CombatStateTypes.REVERSION.getId());

        if (hasReversion) {
            switch (modificationType) {
                case SUM -> modificationType = StatModificationType.SUB;
                case SUB -> modificationType = StatModificationType.SUM;
                case MUL -> modificationType = StatModificationType.DIV;
                case DIV -> modificationType = StatModificationType.MUL;
            }
        }

        int newValue = applyModification(
                baseValue,
                effect.getPower(),
                modificationType
        );

        target.getStats().setStat(stat, newValue);

        return SkillEffectResultDomain.builder()
                .typeResult(SkillEffectResultType.MODIFY_STAT)
                .statAffected(stat)
                .target(target.getType())
                .value((float) newValue)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private int applyModification(int base, float power, StatModificationType type) {

        return switch (type) {
            case SUM -> Math.round(base + power);
            case SUB -> Math.round(base - power);
            case MUL -> Math.round(base * power);
            case DIV -> power != 0 ? Math.round(base / power) : base;
        };
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDomain applyStatus(
            CombatActorDomain attacker,
            CombatActorDomain target,
            SkillEffectDomain effect,
            Long skillId,
            Long combatId
    ) {

        if (attacker != target) {

            StatusResistanceDomain statusSResistance =
                    target.getStatusResistances()
                            .getOrDefault(effect.getStateId(), null);

            float resistanceValue = (statusSResistance != null)
                    ? statusSResistance.getResistance()
                    : 0f;

            boolean hasPrecision = attacker.getActiveStatuses().stream()
                    .anyMatch(s -> s.getStateId() == CombatStateTypes.PRECISION.getId());

            if (!hasPrecision) {
                float chance = 1f - resistanceValue;

                if (random.nextFloat() > chance) {
                    return SkillEffectResultDomain.builder()
                            .typeResult(SkillEffectResultType.MISS_STATUS)
                            .target(target.getType())
                            .build();
                }
            }

        }

        CombatActiveStatusDomain activeStatusDomain = stateService.applyNewState(effect.getStateId(), effect.getStatusDuration(),
                effect.getPower(), effect.getStatAffected(), target, skillId, combatId);

        return SkillEffectResultDomain.builder()
                .typeResult(SkillEffectResultType.STATUS_APPLIED)
                .target(target.getType())
                .appliedStatus(activeStatusDomain)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------
}