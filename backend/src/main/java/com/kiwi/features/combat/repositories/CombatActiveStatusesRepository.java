package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatActiveStatusPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatActiveStatusesRepository extends JpaRepository<CombatActiveStatusPersistence, Long> {

    List<CombatActiveStatusPersistence> findByCombatId(Long combatId);
}