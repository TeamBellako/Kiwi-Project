package com.kiwi.features.combat.data.element;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombatElementRepository extends JpaRepository<CombatElementPersistence, Long> {
}