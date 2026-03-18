package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.CombatActorType;
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
            CombatActorType actor,
            Long skillId
    ) {

        ActorRuntime attacker = context.getActor(actor);
        ActorRuntime target = context.getTarget(actor);

        SkillRuntime skill = attacker.getSkills().get(skillId);

        List<SkillEffectDTO> effects = new ArrayList<>();

        attacker.setLastSkillUsed(skillId);

        // ESTO HAY QUE TENER CUIDADO PORQUE EL ORDEN DEBE IMPORTAR (AÑADIR CAMPO PARA QUE SE ELIJA ORDEN DEFINIR ORDEN FIJO DAMAGE/HEAL/STATUS)
        for (SkillEffectRuntime effect : skill.getEffects()) {

            switch (effect.getEffectType()) {

                case DAMAGE -> effects.add(
                        applyDamage(context, attacker, target, effect)
                );

                case HEAL -> effects.add(
                        applyHeal(attacker, effect)
                );

                case APPLY_STATUS -> {

                    SkillEffectDTO statusEffect =
                            applyStatus(attacker, target, effect);

                    if(statusEffect != null) {
                        effects.add(statusEffect);
                    }
                }
            }
        }

        return CombatActionDTO.builder()
                .actor(actor.name())
                .skillId(skillId)
                .effects(effects)
                .build();
    }

    // ------------------------------------------------

    private SkillEffectDTO applyDamage(
            CombatContext context,
            ActorRuntime attacker,
            ActorRuntime victim,
            SkillEffectRuntime effect
    ) {

        // ACCURACY CHECK
        int hitChance =
                attacker.getAcc()
                        - victim.getEva()
                        + effect.getHitChance();

        int roll = random.nextInt(100);

        if (roll > hitChance) {
            //TODO: ME ACABO DE DAR CUENTA QUE EXISTE MIS Y CRITICO EN LA DB DE LOG OSEA QUE VALE LO ENFOCAMOS ASI, TENGO QUE REVISAR AUN
            // PERO A LO MEJOR SE PUEDE AÑADIR OTRO MAS CONCRETO PARA CUANDO EL ACTOR NO PUEDE ATACAR POR CULPA DE UN ESTADO PORQUE MISS ES AMBIGUO
            return SkillEffectDTO.builder()
                    .type("MISS")
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

            return SkillEffectDTO.builder()
                    .type("HEAL")
                    .target(attacker.getType().name())
                    .value((float) damage)
                    .crit(false)
                    .build();
        }

        victim.damage(damage);

        return SkillEffectDTO.builder()
                .type("DAMAGE")
                .target(victim.getType().name())
                .value((float) damage)
                .crit(crit)
                .build();
    }

    // ------------------------------------------------

    private SkillEffectDTO applyHeal(
            ActorRuntime target,
            SkillEffectRuntime effect
    ) {

        int heal =
                (int) (target.getMaxHp() * effect.getPower());

        target.heal(heal);

        return SkillEffectDTO.builder()
                .type("HEAL")
                .target(target.getType().name())
                .value((float) heal)
                .crit(false)
                .build();
    }

    // ------------------------------------------------

    private SkillEffectDTO applyStatus(
            ActorRuntime attacker,
            ActorRuntime victim,
            SkillEffectRuntime effect
    ) {

        float resistance =
                victim.getStatusResistances()
                        .getOrDefault(effect.getStateId(), 0f);

        float chance =
                1f - resistance;

        if(random.nextFloat() > chance) {
            return null;
        }

        ActiveState state = new ActiveState(
                effect.getStateId(),
                effect.getStatusDuration(),
                effect.getPower()
        );

        victim.getStates().add(state);

        CombatStateAppliedDTO dto =
                stateService.buildStateDTO(state);

        return SkillEffectDTO.builder()
                .type("STATUS")
                .target(victim.getType().name())
                .appliedState(dto)
                .build();
    }
}