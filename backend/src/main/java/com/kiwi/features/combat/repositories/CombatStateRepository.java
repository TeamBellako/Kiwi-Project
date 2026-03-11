package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombatStateRepository extends JpaRepository<CombatStatePersistence, Long> {
}