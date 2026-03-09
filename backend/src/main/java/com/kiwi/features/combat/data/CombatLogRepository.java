package com.kiwi.features.combat.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CombatLogRepository extends JpaRepository<CombatLogPersistence, Long> {
}
