package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.controllers.CombatStateService;
import com.kiwi.features.combat.data.domain.CombatStatusAppliedDomain;
import com.kiwi.features.combat.data.domain.ActorRuntime;
import com.kiwi.features.skills.data.SkillEffectDomain;
import com.kiwi.features.skills.data.SkillCombatDomain;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.ActionType;
import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.SkillEffectResultType;
import com.kiwi.features.skills.data.SkillEffectResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CombatDamageCalculator {

    private final CombatStateService stateService;

    private final Random random = new Random();

    public CombatActionDTO executeSkill(
            CombatContext context,
            CombatActorType actorType,
            Long skillId
    ) {
        // No skills availables
        if(skillId == -1) {

            return CombatActionDTO.builder()
                    .actionType(ActionType.SKIP.name())
                    .actor(actorType.name())
                    .build();
        }

        ActorRuntime attacker = context.getActor(actorType);

        SkillCombatDomain skill = attacker.getSkills().get(skillId);

        List<SkillEffectResultDTO> effectsResults = new ArrayList<>();

        attacker.setLastSkillUsed(skillId);

        //todo: ESTO HAY QUE TENER CUIDADO PORQUE EL ORDEN DEBE IMPORTAR (AÑADIR CAMPO PARA QUE SE ELIJA ORDEN DEFINIR ORDEN FIJO DAMAGE/HEAL/STATUS)
        for (SkillEffectDomain effect : skill.getEffects()) {

            ActorRuntime target = context.getTarget(actorType, effect.getTarget());

            switch (effect.getEffectType()) {

                case DAMAGE -> effectsResults.add(
                        applyDamage(attacker, target, effect)
                );

                case HEAL -> effectsResults.add(
                        applyHeal(target, effect)
                );

                case APPLY_STATUS -> {

                    SkillEffectResultDTO statusEffect =
                            applyStatus(attacker, target, effect, skillId, context.getCombat().getId());

                    if(statusEffect != null) {
                        effectsResults.add(statusEffect);
                    }
                }
            }
        }

        return CombatActionDTO.builder()
                .actionType(ActionType.SKILL_USED.name())
                .actor(actorType.name())
                .skillName(skill.getName())
                .effects(effectsResults)
                .build();
    }

    // ------------------------------------------------

    private SkillEffectResultDTO applyDamage(
            ActorRuntime attacker,
            ActorRuntime victim,
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
                    .type(SkillEffectResultType.MISS.name())
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
        float elementMultiplier =
                victim.getElementMultipliers()
                        .getOrDefault(effect.getElementId(), 1f);

        // STATE MULTIPLIER
        float stateMultiplier =
                stateService.calculateStateMultiplier(attacker, victim);

        float modifiers =
                variance
                        * critMultiplier
                        * elementMultiplier
                        * stateMultiplier;

        int damage =
                (int) (baseDamage * modifiers);

        // ABSORB
        if (elementMultiplier == -1f) {

            attacker.heal(damage);

            return SkillEffectResultDTO.builder()
                    .type(SkillEffectResultType.HEAL.name())
                    .target(attacker.getType().name())
                    .value((float) damage)
                    .critic(false)
                    .build();
        }

        victim.damage(damage);

        return SkillEffectResultDTO.builder()
                .type(SkillEffectResultType.DAMAGE.name())
                .target(victim.getType().name())
                .value((float) damage)
                .critic(crit)
                .build();
    }

    // ------------------------------------------------

    private SkillEffectResultDTO applyHeal(
            ActorRuntime target,
            SkillEffectDomain effect
    ) {

        int heal =
                (int) (target.getMaxHp() * effect.getPower());

        target.heal(heal);

        return SkillEffectResultDTO.builder()
                .type(SkillEffectResultType.HEAL.name())
                .target(target.getType().name())
                .value((float) heal)
                .critic(false)
                .build();
    }

    // ------------------------------------------------

    private SkillEffectResultDTO applyStatus(
            ActorRuntime attacker,
            ActorRuntime target,
            SkillEffectDomain effect,
            Long skillId,
            Long combatId
    ) {

        if (attacker != target) {
            float resistance =
                    target.getStatusResistances()
                            .getOrDefault(effect.getStateId(), 0f);

            float chance = 1f - resistance;

            if (random.nextFloat() > chance) {
                return null;
            }
        }

        CombatStatusAppliedDomain state = new CombatStatusAppliedDomain(
                effect.getStateId(),
                effect.getStateName(),
                effect.getStatusDuration(),
                effect.getPower()
        );

        CombatStatusAppliedDTO stateDTO = stateService.applyNewState(state, target, skillId, combatId);

        return SkillEffectResultDTO.builder()
                .type(SkillEffectResultType.STATUS_APPLIED.name())
                .target(target.getType().name())
                .statusApplied(stateDTO)
                .build();
    }
}