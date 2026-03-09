package com.kiwi.features.combat.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CombatRepository extends JpaRepository<CombatPersistence, Long> {
}