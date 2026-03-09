package com.kiwi.features.combat.data.state;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombatStateRepository extends JpaRepository<CombatStatePersistence, Long> {
}