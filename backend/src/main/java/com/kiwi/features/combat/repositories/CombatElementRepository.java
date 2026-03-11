package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatElementPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombatElementRepository extends JpaRepository<CombatElementPersistence, Long> {
}