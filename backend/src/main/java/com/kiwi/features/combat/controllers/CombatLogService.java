package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.CombatActionDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.mappers.CombatActionMapper;
import com.kiwi.features.combat.data.persistence.*;
import com.kiwi.features.combat.repositories.CombatLogRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class CombatLogService {

    private final CombatLogRepository combatLogRepository;

    //------------------------------------------------------------------------------------------------------------------

    public CombatLogService(CombatLogRepository combatLogRepository) {
        this.combatLogRepository = combatLogRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<CombatActionDTO> getCombatLog(Long combatId){

        List<CombatActionDTO> actions = new ArrayList<>();
        List<CombatLogPersistence> actionsPersistence = combatLogRepository.findByCombatIdOrderByIdAsc(combatId);

        for (CombatLogPersistence combatLogPersistence : actionsPersistence) {
            CombatActionDTO action = CombatActionMapper.toDTO(combatLogPersistence);
            actions.add(action);
        }

        return actions;
    }

    //------------------------------------------------------------------------------------------------------------------

    void deleteCombatLog(Long combatId){

        combatLogRepository.deleteByCombatId(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void saveCombatActions(List<CombatActionDomain> actions, Long combatId, int turnNumber) {
        List<CombatLogPersistence> logs = actions.stream()
                .flatMap(a -> CombatActionMapper.toCombatLogPersistence(a, combatId, turnNumber).stream())
                .toList();

        combatLogRepository.saveAll(logs);
    }

    //------------------------------------------------------------------------------------------------------------------

}