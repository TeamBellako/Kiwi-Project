package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatLogPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombatLogRepository extends JpaRepository<CombatLogPersistence, Long> {
}
