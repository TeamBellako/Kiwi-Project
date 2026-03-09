package com.kiwi.features.combat.data.enemy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnemyElementMultiplierRepository extends JpaRepository<EnemyElementMultiplierPersistence, Long> {

    List<EnemyElementMultiplierPersistence> findByEnemyId(Long enemyId);

}