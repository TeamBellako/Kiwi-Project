package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.CombatActiveStatusDTO;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.mappers.CombatActiveStatusMapper;
import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import com.kiwi.features.combat.data.persistence.CombatActiveStatusPersistence;
import com.kiwi.features.combat.data.domain.CombatActiveStatusDomain;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.exceptions.CombatStateNotFoundException;
import com.kiwi.features.combat.repositories.CombatStateRepository;
import com.kiwi.features.combat.repositories.CombatActiveStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CombatStateService {

    private final CombatStateRepository stateRepository;
    private final CombatActiveStatusRepository activeStatusRepository;

    // ----------------------------------------------------------------------------------------------------------------

    public CombatActiveStatusDTO applyNewState(Long stateId, Integer statusDuration, Float power, CombatActorDomain target, Long skillId, Long combatId)
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

    public List<CombatActiveStatusDomain> getActiveStatus(Long combatId, CombatActorType targetType)
    {
        List<CombatActiveStatusDomain> activeStatusDomainList = new ArrayList<>();

        List<CombatActiveStatusPersistence> activeStatusPersitenceList = activeStatusRepository.findByCombatIdAndTarget(combatId, targetType);

        List<Long> stateIds = activeStatusPersitenceList.stream()
                .map(CombatActiveStatusPersistence::getStateId)
                .distinct()
                .toList();

        Map<Long, CombatStatePersistence> statesMap =
                stateRepository.findByIdIn(stateIds)
                        .stream()
                        .collect(Collectors.toMap(
                                CombatStatePersistence::getId,
                                Function.identity()
                        ));

        for (CombatActiveStatusPersistence activeStatusPersistence : activeStatusPersitenceList) {

            CombatStatePersistence stateInfoPersistence =
                    statesMap.get(activeStatusPersistence.getStateId());

            if (stateInfoPersistence == null) {
                throw new CombatStateNotFoundException(activeStatusPersistence.getStateId());
            }

            CombatActiveStatusDomain activeStatusDomain =
                    CombatActiveStatusMapper.toDomain(activeStatusPersistence, stateInfoPersistence);

            activeStatusDomainList.add(activeStatusDomain);
        }

        return activeStatusDomainList;
    }

    // ----------------------------------------------------------------------------------------------------------------

}