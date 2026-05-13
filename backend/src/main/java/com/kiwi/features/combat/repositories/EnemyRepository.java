package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnemyRepository extends JpaRepository<EnemyPersistence, Long> {
}