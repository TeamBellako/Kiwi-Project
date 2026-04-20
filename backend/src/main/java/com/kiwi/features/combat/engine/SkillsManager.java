package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.controllers.CombatStatesService;
import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.enums.StatModificationType;
import com.kiwi.features.skills.data.domain.SkillEffectDomain;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.CombatActorType;
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

        applyEffects(context, attacker, skill, skillId, effectsResults, SkillEffectType.DAMAGE);
        applyEffects(context, attacker, skill, skillId, effectsResults, SkillEffectType.HEAL);
        applyEffects(context, attacker, skill, skillId, effectsResults, SkillEffectType.MODIFY_STAT);
        applyEffects(context, attacker, skill, skillId, effectsResults, SkillEffectType.APPLY_STATUS);

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
            Long skillId,
            List<SkillEffectResultDomain> results,
            SkillEffectType type
    ) {
        for (SkillEffectDomain effect : skill.getEffects()) {

            if (effect.getEffectType() != type) continue;

            CombatActorDomain target = context.getTarget(attacker.getType(), effect.getTarget());

            switch (type) {
                case DAMAGE -> results.add(
                        applyDamage(attacker, target, effect)
                );

                case HEAL -> results.add(
                        applyHeal(target, effect)
                );

                case MODIFY_STAT -> results.add(
                        applyModifyStat(target, effect)
                );

                case APPLY_STATUS -> {
                    SkillEffectResultDomain statusEffect =
                            applyStatus(attacker, target, effect, skillId, context.getCombat().getId());

                    if (statusEffect != null) {
                        results.add(statusEffect);
                    }
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDomain applyDamage(
            CombatActorDomain attacker,
            CombatActorDomain victim,
            SkillEffectDomain effect
    ) {

        // ACCURACY CHECK
        int hitChance =
                attacker.getStats().getAcc()
                        - victim.getStats().getEva()
                        + effect.getHitChance();

        int roll = random.nextInt(100);

        if (roll > hitChance) {
            return SkillEffectResultDomain.builder()
                    .typeResult(SkillEffectResultType.MISS)
                    .target(victim.getType())
                    .build();
        }

        // ATK / DEF
        float atk =
                effect.getAttackType() == AttackType.PHYSICAL
                        ? attacker.getStats().getPatk()
                        : attacker.getStats().getMatk();

        float def =
                effect.getAttackType() == AttackType.MAGICAL
                        ? victim.getStats().getPdef()
                        : victim.getStats().getMdef();

        float baseDamage =
                (atk / Math.max(def, 1)) * effect.getPower();

        // CRIT
        boolean crit =
                random.nextInt(120) < attacker.getStats().getLck();

        float critMultiplier = crit ? 2f : 1f;

        // RANDOM VARIANCE
        float variance =
                0.9f + random.nextFloat() * 0.2f;

        // ELEMENT
        ElementMultiplierDomain elementMultiplier =
                victim.getElementMultipliers()
                        .getOrDefault(effect.getElementId(), null);

        float elementMultiplierValue = (elementMultiplier != null)
                ? elementMultiplier.getMultiplier()
                : 1f;

        // STATE MULTIPLIER
        float stateMultiplier =
                statusManager.calculateStateMultiplier(attacker, victim);

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

        victim.damage(damage);

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

        int heal = Math.round(target.getStats().getMaxHp() * effect.getPower());

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

        StatsDomain stats = target.getStats();

        float value = effect.getPower();

        int newValue = switch (effect.getStatAffected()) {

            case CURRENT_HP -> applyModification(
                    stats.getCurrentHp(),
                    value,
                    effect.getStatModification()
            );

            case MAX_HP -> applyModification(
                    stats.getMaxHp(),
                    value,
                    effect.getStatModification()
            );

            case PATK -> applyModification(
                    stats.getPatk(),
                    value,
                    effect.getStatModification()
            );

            case MATK -> applyModification(
                    stats.getMatk(),
                    value,
                    effect.getStatModification()
            );

            case PDEF -> applyModification(
                    stats.getPdef(),
                    value,
                    effect.getStatModification()
            );

            case MDEF -> applyModification(
                    stats.getMdef(),
                    value,
                    effect.getStatModification()
            );

            case ACC -> applyModification(
                    stats.getAcc(),
                    value,
                    effect.getStatModification()
            );

            case EVA -> applyModification(
                    stats.getEva(),
                    value,
                    effect.getStatModification()
            );

            case LCK -> applyModification(
                    stats.getLck(),
                    value,
                    effect.getStatModification()
            );
        };

        switch (effect.getStatAffected()) {
            case CURRENT_HP -> stats.setCurrentHp(Math.max(0, newValue));
            case MAX_HP -> stats.setMaxHp(Math.max(1, newValue));
            case PATK -> stats.setPatk(newValue);
            case MATK -> stats.setMatk(newValue);
            case PDEF -> stats.setPdef(newValue);
            case MDEF -> stats.setMdef(newValue);
            case ACC -> stats.setAcc(newValue);
            case EVA -> stats.setEva(newValue);
            case LCK -> stats.setLck(newValue);
        }

        return SkillEffectResultDomain.builder()
                .typeResult(SkillEffectResultType.MODIFY_STAT)
                .statAffected(effect.getStatAffected())
                .target(target.getType())
                .value((float)newValue)
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

            float chance = 1f - resistanceValue;

            if (random.nextFloat() > chance) {
                return null;
            }
        }

        CombatActiveStatusDomain activeStatusDomain = stateService.applyNewState(effect.getStateId(), effect.getStatusDuration(), effect.getPower(), target, skillId, combatId);

        return SkillEffectResultDomain.builder()
                .typeResult(SkillEffectResultType.STATUS_APPLIED)
                .target(target.getType())
                .appliedStatus(activeStatusDomain)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------
}