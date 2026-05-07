package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatBarkTriggerPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatBarkTriggerRepository
        extends JpaRepository<CombatBarkTriggerPersistence, Long> {

    List<CombatBarkTriggerPersistence> findByCombatConfigId(Long combatConfigId);
}
