package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatStatusAppliedDTO;
import com.kiwi.features.combat.data.enums.ActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import com.kiwi.features.combat.data.persistence.CombatStatusAppliedPersistence;
import com.kiwi.features.combat.data.domain.CombatStatusAppliedDomain;
import com.kiwi.features.combat.data.domain.ActorRuntime;
import com.kiwi.features.combat.engine.CombatContext;
import com.kiwi.features.combat.repositories.CombatStateRepository;
import com.kiwi.features.combat.repositories.CombatStatusEffectRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CombatStateService {

    private final CombatStateRepository stateRepository;
    private final CombatStatusEffectRepository statusEffectRepository;

    private final Random random = new Random();

    public CombatStateService(CombatStateRepository stateRepository, CombatStatusEffectRepository statusEffectRepository) {
        this.stateRepository = stateRepository;
        this.statusEffectRepository = statusEffectRepository;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void applyActiveStatesToActor(ActorRuntime actor, CombatContext context)
    {
        // TODO en la BBDD tendrán que tener estos ids
        for (CombatStatusAppliedDomain state : actor.getStates()) {

            if (state.getStateId() == 1) { // BURN
                int damage = applyDamage(actor, state.getValue());

                CombatActionDTO action =
                        CombatActionDTO.builder()
                                .actor(actor.getType().name())
                                .actionType(ActionType.ACTOR_DAMAGED_BY_STATE.name())
                                .stateName(state.getName())
                                .value(damage)
                                .build();

                context.addAction(action);
            }

            if (state.getStateId() == 2) { // POISON
                int damage = applyDamage(actor, state.getValue());

                CombatActionDTO action =
                        CombatActionDTO.builder()
                                .actor(actor.getType().name())
                                .actionType(ActionType.ACTOR_DAMAGED_BY_STATE.name())
                                .stateName(state.getName())
                                .value(damage)
                                .build();

                context.addAction(action);
            }

            if (state.getStateId() == 3) { // FREEZE
                if (random.nextInt(100) < 80) {
                    actor.setActionModifierType(ActionType.ACTOR_BLOCKED_BY_STATE);

                    CombatActionDTO action =
                            CombatActionDTO.builder()
                                    .actor(actor.getType().name())
                                    .actionType(ActionType.ACTOR_BLOCKED_BY_STATE.name())
                                    .stateName(state.getName())
                                    .build();

                    context.addAction(action);
                }
            } else if (state.getStateId() == 4) {  // CONFUSION: no need to apply if FREEZE is applying
                if (actor.getLastSkillUsed() != null && random.nextInt(100) < 40)
                {
                    actor.setActionModifierType(ActionType.SKILL_REPEAT_BY_STATE);

                    CombatActionDTO action =
                            CombatActionDTO.builder()
                                    .actor(actor.getType().name())
                                    .actionType(ActionType.SKILL_REPEAT_BY_STATE.name())
                                    .stateName(state.getName())
                                    .build();

                    context.addAction(action);
                }
            }

            //TODO: lo he dejado asi por simplicidad pero en el futuro al user hay que aplicarlo inmediatamente
            //para que sepa las que no puede usar
            if (state.getStateId() == 7) { //MUTIS

                List<Long> blockedSkillIds = new ArrayList<>();

                List<Long> skillIds =
                        new ArrayList<>(actor.getSkills().keySet());

                Collections.shuffle(skillIds);

                blockedSkillIds = skillIds.stream()
                        .limit(2)
                        .toList();

                CombatActionDTO action =
                        CombatActionDTO.builder()
                                .actor(actor.getType().name())
                                .actionType(ActionType.BLOCKED_SKILLS_BY_STATE.name())
                                .stateName(state.getName())
                                .blockedSkills(blockedSkillIds)
                                .build();

                context.addAction(action);
                actor.setBlockedSkills(blockedSkillIds);

            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    private int applyDamage(ActorRuntime actor, float value)
    {
        int damage = (int) (actor.getMaxHp() * value); // 0.05 TBC
        actor.damage(damage);
        return damage;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public float calculateStateMultiplier(ActorRuntime attacker, ActorRuntime victim)
    {
        float multiplier = 1f;

        for (CombatStatusAppliedDomain s : attacker.getStates()) {

            if (s.getStateId() == 10) { // ATK UP
                multiplier *= 1.5f;
            }

            if (s.getStateId() == 11) { // ATK DOWN
                multiplier *= 0.5f;
            }
        }

        for (CombatStatusAppliedDomain s : victim.getStates()) {

            if (s.getStateId() == 12) { // DEF UP
                multiplier *= 0.5f;
            }

            if (s.getStateId() == 13) { // DEF DOWN
                multiplier *= 1.5f;
            }
        }

        return multiplier;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void reduceStatesTurnsToActor(ActorRuntime actor, CombatContext context)
    {
        Iterator<CombatStatusAppliedDomain> statesIt =
                actor.getStates().iterator();

        while (statesIt.hasNext()) {
            CombatStatusAppliedDomain state = statesIt.next();

            CombatActionDTO action =
                    CombatActionDTO.builder()
                            .actor(actor.getType().name())
                            .actionType(ActionType.STATUS_TURN_REDUCED.name())
                            .stateName(state.getName())
                            .build();

            state.setRemainingTurns(
                    state.getRemainingTurns() - 1
            );

            if (state.getRemainingTurns() <= 0) {
                statesIt.remove();

                action.setActionType(ActionType.STATUS_FINISHED.name());
            }

            context.addAction(action);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public CombatStatusAppliedDTO applyNewState(CombatStatusAppliedDomain stateRuntime, ActorRuntime target, Long skillId, Long combatId)
    {
        Optional<CombatStatePersistence> statePersistence = stateRepository.findById(stateRuntime.getStateId());

        if (statePersistence.isPresent()) {

            target.getStates().add(stateRuntime);

            CombatStatusAppliedPersistence statusEffectPersistence =
                    CombatStatusAppliedPersistence.builder()
                            .combatId(combatId)
                            .sourceSkillId(skillId)
                            .target(target.getType())
                            .stateId(stateRuntime.getStateId())
                            .value(stateRuntime.getValue())
                            .remainingTurns(stateRuntime.getRemainingTurns())
                            .build();

            statusEffectRepository.save(statusEffectPersistence);

            return CombatStatusAppliedDTO.builder()
                    .stateId(stateRuntime.getStateId())
                    .name(stateRuntime.getName())
                    .icon(statePersistence.get().getIcon())
                    .description(statePersistence.get().getDescription())
                    .remainingTurns(stateRuntime.getRemainingTurns())
                    .value(stateRuntime.getValue())
                    .build();
        }
        else {
            throw new IllegalStateException("Combat state does not exist");
        }

    }

    // ----------------------------------------------------------------------------------------------------------------

    public List<CombatStatusAppliedDTO> getCurrentStatusAppliedDTO(Long combatId, CombatActorType targetType)
    {
        List<CombatStatusAppliedDTO> currentSatesDTO = new ArrayList<>();

        List<CombatStatusAppliedPersistence> statusEffectList = statusEffectRepository.findByCombatIdAndTargetType(combatId, targetType);

                for (CombatStatusAppliedPersistence statusEffect : statusEffectList){

                    Optional<CombatStatePersistence> statePersistence = stateRepository.findById(statusEffect.getStateId());

                    if (statePersistence.isPresent()) {

                        CombatStatusAppliedDTO statusDTO =
                                CombatStatusAppliedDTO.builder()
                                .stateId(statusEffect.getStateId())
                                .name(statePersistence.get().getName())
                                .icon(statePersistence.get().getIcon())
                                .description(statePersistence.get().getDescription())
                                .remainingTurns(statusEffect.getRemainingTurns())
                                .value(statusEffect.getValue())
                                .build();

                        currentSatesDTO.add(statusDTO);
                    }
                    else
                    {
                        throw new IllegalStateException("Combat state does not exist");
                    }
                }



        return currentSatesDTO;
    }

}