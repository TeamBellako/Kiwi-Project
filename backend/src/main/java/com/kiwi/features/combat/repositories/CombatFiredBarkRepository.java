package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatFiredBarkKey;
import com.kiwi.features.combat.data.persistence.CombatFiredBarkPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatFiredBarkRepository
        extends JpaRepository<CombatFiredBarkPersistence, CombatFiredBarkKey> {

    List<CombatFiredBarkPersistence> findById_CombatId(Long combatId);

    void deleteByIdCombatId(Long combatId);
}
