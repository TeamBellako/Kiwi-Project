package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatLogPersistence;
import com.kiwi.features.combat.data.enums.CombatActorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CombatLogRepository extends JpaRepository<CombatLogPersistence, Long> {

    @Query("SELECT cl FROM CombatLogPersistence cl WHERE cl.combatId = :combatId ORDER BY cl.turnNumber ASC, cl.id ASC")
    List<CombatLogPersistence> findAllByCombatId(@Param("combatId") Long combatId);

    @Query("""
       SELECT cl.skillId
       FROM CombatLogPersistence cl
       WHERE cl.combatId = :combatId
         AND cl.actor = :actor
         AND cl.skillId IS NOT NULL
       ORDER BY cl.turnNumber DESC, cl.id DESC
       """)
    Optional<Long> findLastSkillUsedByActorSkillId(
            @Param("combatId") Long combatId,
            @Param("actor") CombatActorType actor
    );
}