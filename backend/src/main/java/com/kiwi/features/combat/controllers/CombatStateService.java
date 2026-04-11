package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatActiveStatusDTO;
import com.kiwi.features.combat.data.enums.ActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.mappers.CombatActiveStatusMapper;
import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import com.kiwi.features.combat.data.persistence.CombatActiveStatusPersistence;
import com.kiwi.features.combat.data.domain.CombatActiveStatusDomain;
import com.kiwi.features.combat.data.domain.ActorDomain;
import com.kiwi.features.combat.engine.CombatContext;
import com.kiwi.features.combat.exceptions.CombatStateNotFoundException;
import com.kiwi.features.combat.repositories.CombatStateRepository;
import com.kiwi.features.combat.repositories.CombatActiveStatusRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CombatStateService {

    private final CombatStateRepository stateRepository;
    private final CombatActiveStatusRepository activeStatusRepository;

    private final Random random = new Random();

    public CombatStateService(CombatStateRepository stateRepository, CombatActiveStatusRepository activeStatusRepository) {
        this.stateRepository = stateRepository;
        this.activeStatusRepository = activeStatusRepository;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void applyActiveStatesToActor(ActorDomain actor, CombatContext context)
    {
        // TODO en la BBDD tendrán que tener estos ids
        for (CombatActiveStatusDomain state : actor.getStates()) {

            if (state.getStateId() == 1) { // BURN
                int damage = applyDamage(actor, state.getValue());

                CombatActionDTO action =
                        CombatActionDTO.builder()
                                .actor(actor.getType().name())
                                .actionType(ActionType.ACTOR_DAMAGED_BY_STATE.name())
                                .stateName(state.getName())
                                .value((float)damage)
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
                                .value((float)damage)
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

                List<Long> blockedSkillIds;

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

                blockedSkillIds.forEach(actor.getSkills()::remove);
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    private int applyDamage(ActorDomain actor, Float value)
    {
        int damage = Math.round(actor.getMaxHp() * value);
        actor.damage(damage);
        return damage;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public float calculateStateMultiplier(ActorDomain attacker, ActorDomain victim)
    {
        float multiplier = 1f;

        for (CombatActiveStatusDomain s : attacker.getStates()) {

            if (s.getStateId() == 10) { // ATK UP
                multiplier *= 1.5f;
            }

            if (s.getStateId() == 11) { // ATK DOWN
                multiplier *= 0.5f;
            }
        }

        for (CombatActiveStatusDomain s : victim.getStates()) {

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

    public void reduceStatesTurnsToActor(ActorDomain actor, CombatContext context)
    {
        Iterator<CombatActiveStatusDomain> statesIt =
                actor.getStates().iterator();

        while (statesIt.hasNext()) {
            CombatActiveStatusDomain state = statesIt.next();

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

    public CombatActiveStatusDTO applyNewState(Long stateId, Integer statusDuration, Float power, ActorDomain target, Long skillId, Long combatId)
    {
        CombatStatePersistence statePersistence =
                        stateRepository.findById(stateId)
                        .orElseThrow(() -> new CombatStateNotFoundException(stateId));

        CombatActiveStatusPersistence statusEffectPersistence =
                    CombatActiveStatusPersistence.builder()
                            .combatId(combatId)
                            .sourceSkillId(skillId)
                            .target(target.getType())
                            .stateId(stateId)
                            .value(power)
                            .remainingTurns(statusDuration)
                            .build();

        activeStatusRepository.save(statusEffectPersistence);

        CombatActiveStatusDomain stateDomain = CombatActiveStatusMapper.toDomain(statusEffectPersistence, statePersistence);
        target.getStates().add(stateDomain);

            return CombatActiveStatusMapper.toDTO(stateDomain);
    }

    // ----------------------------------------------------------------------------------------------------------------

    public List<CombatActiveStatusDomain> getActiveStatusDomain(Long combatId, CombatActorType targetType)
    {
        List<CombatActiveStatusDomain> activeStatusDomainList = new ArrayList<>();

        List<CombatActiveStatusPersistence> activeStatusPersitenceList = activeStatusRepository.findByCombatIdAndTargetType(combatId, targetType);

                for (CombatActiveStatusPersistence activeStatusPersistence : activeStatusPersitenceList)
                {
                    CombatStatePersistence stateInfoPersistence =
                        stateRepository.findById(activeStatusPersistence.getStateId())
                                .orElseThrow(() -> new CombatStateNotFoundException(activeStatusPersistence.getStateId()));

                        CombatActiveStatusDomain activeStatusDomain = CombatActiveStatusMapper.toDomain(activeStatusPersistence, stateInfoPersistence);
                        activeStatusDomainList.add(activeStatusDomain);
                }

        return activeStatusDomainList;
    }

}