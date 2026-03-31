package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.repositories.CombatLogRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CombatLogService {

    private final CombatLogRepository combatLogRepository;

    public CombatLogService(CombatLogRepository combatLogRepository) {
        this.combatLogRepository = combatLogRepository;
    }

    //TODO
    public List<CombatActionDTO> getCombatLog(Long combatId){

        List<CombatActionDTO> actions = new ArrayList<>();
      //  combatLogRepository.findByCombatIdOrderById

        return actions;
    }

    //TODO
    public Long getLastSkillUsed (Long combatId, CombatActorType actor){

        return -1L;
    }

}