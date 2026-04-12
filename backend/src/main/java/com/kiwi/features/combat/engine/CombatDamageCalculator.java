package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.controllers.CombatStateService;
import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.skills.data.domain.SkillEffectDomain;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
import com.kiwi.features.skills.data.DTO.SkillEffectResultDTO;
import com.kiwi.features.skills.data.enums.SkillEffectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CombatDamageCalculator {

    private final CombatStateService stateService;
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
                attacker.getAcc()
                        - victim.getEva()
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
                        ? attacker.getPatk()
                        : attacker.getMatk();

        float def =
                effect.getAttackType() == AttackType.MAGICAL
                        ? victim.getPdef()
                        : victim.getMdef();

        float baseDamage =
                (atk / Math.max(def, 1)) * effect.getPower();

        // CRIT
        boolean crit =
                random.nextInt(120) < attacker.getLck();

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

        int heal = Math.round(target.getMaxHp() * effect.getPower());

        target.heal(heal);

        return SkillEffectResultDomain.builder()
                .typeResult(SkillEffectResultType.HEAL)
                .target(target.getType())
                .value((float)heal)
                .critic(false)
                .build();
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