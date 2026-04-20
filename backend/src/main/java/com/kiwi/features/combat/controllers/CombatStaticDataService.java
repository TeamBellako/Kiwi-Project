package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.ElementMultiplierDomain;
import com.kiwi.features.combat.data.domain.StatusResistanceDomain;
import com.kiwi.features.combat.data.mappers.ElementMultiplierMapper;
import com.kiwi.features.combat.data.mappers.StatusResistanceMapper;
import com.kiwi.features.combat.data.persistence.*;
import com.kiwi.features.combat.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CombatStaticDataService {

    private final CombatElementRepository combatElementRepository;
    private final UserElementMultiplierRepository userElementMultiplierRepository;
    private final EnemyElementMultiplierRepository enemyElementMultiplierRepository;
    private final UserStatusResistanceRepository userStatusResistanceRepository;
    private final EnemyStatusResistanceRepository enemyStatusResistanceRepository;

    //------------------------------------------------------------------------------------------------------------------

    public CombatStaticDataService(CombatElementRepository combatElementRepository, CombatStatesRepository combatStatesRepository, UserElementMultiplierRepository userElementMultiplierRepository, EnemyElementMultiplierRepository enemyElementMultiplierRepository, UserStatusResistanceRepository userStatusResistanceRepository, EnemyStatusResistanceRepository enemyStatusResistanceRepository) {
        this.combatElementRepository = combatElementRepository;
        this.userElementMultiplierRepository = userElementMultiplierRepository;
        this.enemyElementMultiplierRepository = enemyElementMultiplierRepository;
        this.userStatusResistanceRepository = userStatusResistanceRepository;
        this.enemyStatusResistanceRepository = enemyStatusResistanceRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    public Map<Long, CombatElementPersistence> loadElementsMap() {
        return combatElementRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CombatElementPersistence::getId,
                        Function.identity()
                ));
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<ElementMultiplierDomain> loadUserElements(
            Long userId,
            Map<Long, CombatElementPersistence> elementsMap
    ) {

        List<UserElementMultiplierPersistence> elementMultipliers =
                userElementMultiplierRepository.findByIdUserId(userId);

        return elementMultipliers.stream()
                .map(multiplier -> {

                    CombatElementPersistence element =
                            elementsMap.get(multiplier.getId().getElementId());

                    return ElementMultiplierMapper.toDomain(multiplier, element);
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<ElementMultiplierDomain> loadEnemyElements(
            Long enemyId,
            Map<Long, CombatElementPersistence> elementsMap
    ) {

        List<EnemyElementMultiplierPersistence> elementMultipliers =
                enemyElementMultiplierRepository.findByIdEnemyId(enemyId);

        return elementMultipliers.stream()
                .map(multiplier -> {

                    CombatElementPersistence element =
                            elementsMap.get(multiplier.getId().getElementId());

                    return ElementMultiplierMapper.toDomain(multiplier, element);
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<StatusResistanceDomain> loadUserResistances(
            Long userId,
            Map<Long, CombatStatePersistence> statesMap
    ) {

        List<UserStatusResistancePersistence> resistances =
                userStatusResistanceRepository.findByIdUserId(userId);

        return resistances.stream()
                .map(resistance -> {

                    CombatStatePersistence state =
                            statesMap.get(resistance.getId().getStateId());

                    return StatusResistanceMapper.toDomain(resistance, state);
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<StatusResistanceDomain> loadEnemyResistances(
            Long enemyId,
            Map<Long, CombatStatePersistence> statesMap
    ) {

        List<EnemyStatusResistancePersistence> resistances =
                enemyStatusResistanceRepository.findByIdEnemyId(enemyId);

        return resistances.stream()
                .map(resistance -> {

                    CombatStatePersistence state =
                            statesMap.get(resistance.getId().getStateId());
                    return StatusResistanceMapper.toDomain(resistance, state);
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------
}
