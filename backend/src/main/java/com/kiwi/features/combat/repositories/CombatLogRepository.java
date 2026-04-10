package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatLogPersistence;
import com.kiwi.features.combat.data.enums.CombatActorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CombatLogRepository extends JpaRepository<CombatLogPersistence, Long> {

    List<CombatLogPersistence> findByCombatIdOrderByIdAsc(Long combatId);

    void deleteByCombatId(Long combatId);
}