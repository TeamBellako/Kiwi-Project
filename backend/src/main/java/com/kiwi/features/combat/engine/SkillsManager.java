package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.controllers.CombatStatesService;
import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.enums.*;
import com.kiwi.features.skills.data.domain.SkillEffectDomain;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.skills.data.domain.SkillEffectResultDomain;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
import com.kiwi.features.skills.data.enums.SkillEffectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
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

        SkillEffectContext effectContext = new SkillEffectContext(1f);

        List<SkillEffectResultDomain> effectsResults = new ArrayList<>();

        attacker.setLastSkillUsed(skillId);

        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.RESET_COOLDOWNS, effectContext);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.APPLY_STATUS, effectContext);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.EXTEND_BUFFS, effectContext);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.SWAP_BUFFS, effectContext);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.COPY_BUFFS, effectContext);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.CONSUME_STATUS, effectContext);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.DAMAGE, effectContext);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.HEAL, effectContext);
        applyEffects(context, attacker, skill, effectsResults, SkillEffectType.MODIFY_STAT, effectContext);

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
            SkillEffectType type,
            SkillEffectContext effectContext
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
                case RESET_COOLDOWNS -> results.add(
                        applyResetCooldowns(attacker)
                );

                case APPLY_STATUS -> results.add(
                        applyStatus(attacker, victim, effect, skill.getId(), context.getCombat().getId())
                );

                case SWAP_BUFFS -> results.addAll(
                                applySwapBuffs(attacker, victim)
                );

                case COPY_BUFFS -> results.addAll(
                                applyCopyBuffs(attacker, victim)
                );

                case CONSUME_STATUS -> results.addAll(
                                applyConsumeStatus(victim,effect,effectContext)
                );

                case EXTEND_BUFFS -> results.addAll(
                                applyExtendBuffs(victim, effect)
                );

                case DAMAGE -> results.add(
                        applyDamage(context, attacker, victim, effect, skill.getElementId(), effectContext)
                );

                case HEAL -> results.add(
                        applyHeal(victim, effect, effectContext)
                );

                case MODIFY_STAT -> results.add(
                        applyModifyStat(victim, effect, effectContext)
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
            Long skillElementId,
            SkillEffectContext effectContext
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
        float stateMultiplier = attackerAtk * victimDef;

        float modifiers =
                        variance
                        * critMultiplier
                        * elementMultiplierValue
                        * stateMultiplier;

        int damage = Math.round(
                        baseDamage
                        * modifiers
                        * effectContext.getConsumeMultiplier()
        );

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
            SkillEffectDomain effect,
            SkillEffectContext effectContext
    ) {

        int heal = Math.round(
                        target.getStats().getStat(StatType.MAX_HP)
                        * effect.getPower()
                        * effectContext.getConsumeMultiplier()
        );

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
            SkillEffectDomain effect,
            SkillEffectContext effectContext
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
                effect.getPower() * effectContext.getConsumeMultiplier(),
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

    private List<SkillEffectResultDomain> applyConsumeStatus(
            CombatActorDomain target,
            SkillEffectDomain effect,
            SkillEffectContext effectContext
    ) {

        List<SkillEffectResultDomain> results =
                new ArrayList<>();

        int consumedTurns = 0;

        Iterator<CombatActiveStatusDomain> it =
                target.getActiveStatuses().iterator();

        while (it.hasNext()) {

            CombatActiveStatusDomain status = it.next();

            int consumeTurns = effect.getTurns();

            if (consumeTurns >= status.getRemainingTurns()) {

                consumedTurns += status.getRemainingTurns();

                it.remove();

                results.add(
                        SkillEffectResultDomain.builder()
                                .typeResult(SkillEffectResultType.STATUS_REMOVED)
                                .target(target.getType())
                                .appliedStatus(status)
                                .build()
                );

            } else {

                consumedTurns += consumeTurns;

                status.setRemainingTurns(
                        status.getRemainingTurns() - consumeTurns
                );

                results.add(
                        SkillEffectResultDomain.builder()
                                .typeResult(SkillEffectResultType.STATUS_CONSUMED)
                                .target(target.getType())
                                .appliedStatus(status)
                                .turns(consumeTurns)
                                .build()
                );
            }
        }

        effectContext.setConsumeMultiplier(
                effectContext.getConsumeMultiplier() + consumedTurns
        );

        return results;
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<SkillEffectResultDomain> applyExtendBuffs(
            CombatActorDomain target,
            SkillEffectDomain effect
    ) {

        List<SkillEffectResultDomain> results =
                new ArrayList<>();

        for (CombatActiveStatusDomain status : target.getActiveStatuses()) {

            status.setRemainingTurns(
                    status.getRemainingTurns()
                            + effect.getTurns()
            );

            results.add(
                    SkillEffectResultDomain.builder()
                            .typeResult(SkillEffectResultType.STATUS_EXTENDED)
                            .target(target.getType())
                            .appliedStatus(status)
                            .turns(effect.getTurns())
                            .build()
            );
        }

        return results;
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<SkillEffectResultDomain> applyCopyBuffs(
            CombatActorDomain attacker,
            CombatActorDomain target
    ) {

        attacker.getActiveStatuses().clear();

        for (CombatActiveStatusDomain status : target.getActiveStatuses()) {

            CombatActiveStatusDomain copied =
                    CombatActiveStatusDomain.builder()
                            .stateId(status.getStateId())
                            .remainingTurns(status.getRemainingTurns())
                            .value(status.getValue())
                            .statAffected(status.getStatAffected())
                            .build();

            attacker.getActiveStatuses().add(copied);
        }

        return List.of(
                SkillEffectResultDomain.builder()
                        .typeResult(SkillEffectResultType.BUFFS_COPIED)
                        .target(attacker.getType())
                        .build()
        );
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<SkillEffectResultDomain> applySwapBuffs(
            CombatActorDomain attacker,
            CombatActorDomain target
    ) {

        List<CombatActiveStatusDomain> attackerStatuses =
                new ArrayList<>(attacker.getActiveStatuses());

        List<CombatActiveStatusDomain> targetStatuses =
                new ArrayList<>(target.getActiveStatuses());

        attacker.getActiveStatuses().clear();
        target.getActiveStatuses().clear();

        attacker.getActiveStatuses().addAll(targetStatuses);
        target.getActiveStatuses().addAll(attackerStatuses);

        return List.of(
                SkillEffectResultDomain.builder()
                        .typeResult(SkillEffectResultType.BUFFS_SWAPPED)
                        .target(target.getType())
                        .build()
        );
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDomain applyResetCooldowns(
            CombatActorDomain attacker
    ) {

        List<Long> resetCooldownSkills = new ArrayList<>();

        for (SkillCombatDomain skill : attacker.getSkills().values()) {
            resetCooldownSkills.add(skill.getId());
            attacker.getResetCooldownSkills().add(skill.getId());
        }

        return SkillEffectResultDomain.builder()
                .typeResult(SkillEffectResultType.RESET_COOLDOWNS)
                .target(attacker.getType())
                .resetCooldownSkills(resetCooldownSkills)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}