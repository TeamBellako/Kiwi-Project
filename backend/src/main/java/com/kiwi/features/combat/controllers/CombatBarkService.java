package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.persistence.CombatBarkTriggerPersistence;
import com.kiwi.features.combat.data.persistence.CombatFiredBarkKey;
import com.kiwi.features.combat.data.persistence.CombatFiredBarkPersistence;
import com.kiwi.features.combat.repositories.CombatBarkTriggerRepository;
import com.kiwi.features.combat.repositories.CombatFiredBarkRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombatBarkService {

    private final CombatBarkTriggerRepository triggerRepository;
    private final CombatFiredBarkRepository firedRepository;

    //------------------------------------------------------------------------------------------------------------------

    public CombatBarkService(
            CombatBarkTriggerRepository triggerRepository,
            CombatFiredBarkRepository firedRepository
    ) {
        this.triggerRepository = triggerRepository;
        this.firedRepository = firedRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<CombatBarkTriggerPersistence> getTriggersForConfig(Long combatConfigId) {

        return triggerRepository.findByCombatConfigId(combatConfigId);
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<Long> getFiredTriggerIds(Long combatId) {

        return firedRepository.findById_CombatId(combatId).stream()
                .map(e -> e.getId().getTriggerId())
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean triggerBelongsToConfig(Long triggerId, Long combatConfigId) {

        return triggerRepository.findById(triggerId)
                .map(t -> t.getCombatConfigId().equals(combatConfigId))
                .orElse(false);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void markFired(Long combatId, Long triggerId) {

        CombatFiredBarkKey key = new CombatFiredBarkKey(combatId, triggerId);

        if (firedRepository.existsById(key)) {
            return;
        }

        firedRepository.save(CombatFiredBarkPersistence.builder().id(key).build());
    }

    //------------------------------------------------------------------------------------------------------------------

    public void deleteByCombatId(Long combatId) {

        firedRepository.deleteByIdCombatId(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------
}
