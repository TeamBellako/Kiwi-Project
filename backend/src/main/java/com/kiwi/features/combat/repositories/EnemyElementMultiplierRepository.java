package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.EnemyElementMultiplierPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnemyElementMultiplierRepository extends JpaRepository<EnemyElementMultiplierPersistence, Long> {

    List<EnemyElementMultiplierPersistence> findByEnemyId(Long enemyId);

}