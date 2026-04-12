package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.controllers.CombatStateService;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.ElementMultiplierDomain;
import com.kiwi.features.combat.data.domain.StatusResistanceDomain;
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

    public CombatActionDTO executeSkill(
            CombatContext context,
            CombatActorType actorType,
            Long skillId
    ) {
        // No skills available
        if(skillId == -1) {

            return CombatActionDTO.builder()
                    .actionType(CombatActionType.SKIP.name())
                    .actor(actorType.name())
                    .build();
        }

        CombatActorDomain attacker = context.getActor(actorType);

        SkillCombatDomain skill = attacker.getSkills().get(skillId);

        if(skill == null) {
            throw new IllegalStateException("Skill does not exist.");
        }

        List<SkillEffectResultDTO> effectsResults = new ArrayList<>();

        attacker.setLastSkillUsed(skillId);

        applyEffects(context, attacker, skill, skillId, effectsResults, SkillEffectType.DAMAGE);
        applyEffects(context, attacker, skill, skillId, effectsResults, SkillEffectType.HEAL);
        applyEffects(context, attacker, skill, skillId, effectsResults, SkillEffectType.APPLY_STATUS);

        return CombatActionDTO.builder()
                .actionType(CombatActionType.SKILL_USED.name())
                .actor(actorType.name())
                .skillName(skill.getName())
                .effects(effectsResults)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void applyEffects(
            CombatContext context,
            CombatActorDomain attacker,
            SkillCombatDomain skill,
            Long skillId,
            List<SkillEffectResultDTO> results,
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
                    SkillEffectResultDTO statusEffect =
                            applyStatus(attacker, target, effect, skillId, context.getCombat().getId());

                    if (statusEffect != null) {
                        results.add(statusEffect);
                    }
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDTO applyDamage(
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
            return SkillEffectResultDTO.builder()
                    .typeResult(SkillEffectResultType.MISS.name())
                    .target(victim.getType().name())
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

            return SkillEffectResultDTO.builder()
                    .typeResult(SkillEffectResultType.HEAL.name())
                    .target(attacker.getType().name())
                    .value((float)damage)
                    .critic(false)
                    .build();
        }

        victim.damage(damage);

        return SkillEffectResultDTO.builder()
                .typeResult(SkillEffectResultType.DAMAGE.name())
                .target(victim.getType().name())
                .value((float)damage)
                .critic(crit)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDTO applyHeal(
            CombatActorDomain target,
            SkillEffectDomain effect
    ) {

        int heal = Math.round(target.getMaxHp() * effect.getPower());

        target.heal(heal);

        return SkillEffectResultDTO.builder()
                .typeResult(SkillEffectResultType.HEAL.name())
                .target(target.getType().name())
                .value((float)heal)
                .critic(false)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private SkillEffectResultDTO applyStatus(
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

        CombatActiveStatusDTO activeStatusDTO = stateService.applyNewState(effect.getStateId(), effect.getStatusDuration(), effect.getPower(), target, skillId, combatId);

        return SkillEffectResultDTO.builder()
                .typeResult(SkillEffectResultType.STATUS_APPLIED.name())
                .target(target.getType().name())
                .appliedStatus(activeStatusDTO)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------
}