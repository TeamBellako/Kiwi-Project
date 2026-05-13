package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CombatStatesRepository extends JpaRepository<CombatStatePersistence, Long> {

    List<CombatStatePersistence> findByIdIn(List<Long> ids);
}