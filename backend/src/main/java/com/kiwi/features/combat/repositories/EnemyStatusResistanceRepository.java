package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.EnemyStatusResistancePersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnemyStatusResistanceRepository extends JpaRepository<EnemyStatusResistancePersistence, Long> {

    List<EnemyStatusResistancePersistence> findByIdEnemyId(Long enemyId);

}