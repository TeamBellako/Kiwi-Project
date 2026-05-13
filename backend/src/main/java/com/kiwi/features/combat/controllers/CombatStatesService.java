package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.combat.data.mappers.CombatActiveStatusMapper;
import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import com.kiwi.features.combat.data.persistence.CombatActiveStatusPersistence;
import com.kiwi.features.combat.data.domain.CombatActiveStatusDomain;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.exceptions.CombatStateNotFoundException;
import com.kiwi.features.combat.repositories.CombatStatesRepository;
import com.kiwi.features.combat.repositories.CombatActiveStatusesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CombatStatesService {

    private final CombatStatesRepository statesRepository;
    private final CombatActiveStatusesRepository activeStatusesRepository;

    // ----------------------------------------------------------------------------------------------------------------

    public CombatActiveStatusDomain applyNewState(Long stateId, Integer statusDuration, Float power, StatType statAffected,
                                                  CombatActorDomain target, Long skillId, Long combatId)
    {
        CombatStatePersistence statePersistence =
                        statesRepository.findById(stateId)
                        .orElseThrow(() -> new CombatStateNotFoundException(stateId));

        CombatActiveStatusPersistence statusEffectPersistence =
                    CombatActiveStatusPersistence.builder()
                            .combatId(combatId)
                            .sourceSkillId(skillId)
                            .target(target.getType())
                            .stateId(stateId)
                            .value(power)
                            .statAffected(statAffected)
                            .remainingTurns(statusDuration)
                            .build();

        activeStatusesRepository.save(statusEffectPersistence);

        CombatActiveStatusDomain stateDomain = CombatActiveStatusMapper.toDomain(statusEffectPersistence, statePersistence);
        target.getActiveStatuses().add(stateDomain);

            return stateDomain;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public Map<Long, CombatStatePersistence> loadStatesMap() {
        return statesRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CombatStatePersistence::getId,
                        Function.identity()
                ));
    }

    // ----------------------------------------------------------------------------------------------------------------

    public Map<CombatActorType, List<CombatActiveStatusDomain>> getActiveStatuses(Long combatId)
    {
        Map<CombatActorType, List<CombatActiveStatusDomain>> result = new HashMap<>();

        List<CombatActiveStatusPersistence> list =
                activeStatusesRepository.findByCombatId(combatId);

        List<Long> stateIds = list.stream()
                .map(CombatActiveStatusPersistence::getStateId)
                .distinct()
                .toList();

        Map<Long, CombatStatePersistence> statesMap =
                statesRepository.findByIdIn(stateIds)
                        .stream()
                        .collect(Collectors.toMap(
                                CombatStatePersistence::getId,
                                Function.identity()
                        ));

        for (CombatActiveStatusPersistence p : list) {

            CombatStatePersistence state = statesMap.get(p.getStateId());

            if (state == null) {
                throw new CombatStateNotFoundException(p.getStateId());
            }

            CombatActiveStatusDomain domain =
                    CombatActiveStatusMapper.toDomain(p, state);

            result.computeIfAbsent(p.getTarget(), k -> new ArrayList<>())
                    .add(domain);
        }

        return result;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public List<CombatActiveStatusDomain> getActiveStatusesForActor(
            Map<CombatActorType, List<CombatActiveStatusDomain>> activeStatuses,
            CombatActorType combatActorType
    ) {
        if (activeStatuses == null || combatActorType == null) {
            return List.of();
        }

        return activeStatuses.getOrDefault(combatActorType, List.of());
    }

    // ----------------------------------------------------------------------------------------------------------------

}