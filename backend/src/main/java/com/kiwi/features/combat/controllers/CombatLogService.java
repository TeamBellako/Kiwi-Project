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
import java.util.Objects;

@Component
public class CombatLogService {

    private final CombatLogRepository combatLogRepository;

    //------------------------------------------------------------------------------------------------------------------

    public CombatLogService(CombatLogRepository combatLogRepository) {
        this.combatLogRepository = combatLogRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<CombatActionDTO> getCombatLog(Long combatId){

        List<CombatLogPersistence> logs =
                combatLogRepository.findByCombatIdOrderByIdAsc(combatId);

        List<CombatActionDTO> result = new ArrayList<>();

        List<CombatLogPersistence> currentGroup = new ArrayList<>();

        for (CombatLogPersistence log : logs) {

            if (currentGroup.isEmpty()) {
                currentGroup.add(log);
                continue;
            }

            CombatLogPersistence first = currentGroup.get(0);

            if (isSameAction(first, log)) {
                currentGroup.add(log);
            } else {
                result.add(CombatActionMapper.toDTOGroup(currentGroup));
                currentGroup.clear();
                currentGroup.add(log);
            }
        }

        if (!currentGroup.isEmpty()) {
            result.add(CombatActionMapper.toDTOGroup(currentGroup));
        }

        return result;
    }

    private boolean isSameAction(CombatLogPersistence a, CombatLogPersistence b) {
        return a.getTurnNumber() == b.getTurnNumber()
                && a.getActor() == b.getActor()
                && a.getCombatActionType() == b.getCombatActionType()
                && Objects.equals(a.getSkillName(), b.getSkillName());
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