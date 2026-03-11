package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatStatusEffectPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatStatusEffectRepository extends JpaRepository<CombatStatusEffectPersistence, Long> {

    List<CombatStatusEffectPersistence> findByCombatId(Long combatId);
}