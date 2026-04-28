package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CombatRepository extends JpaRepository<CombatPersistence, Long> {

    Optional<CombatPersistence> findByUserIdAndCombatConfigId(Long userId, Long combatConfigId);

    Optional<CombatPersistence> findFirstByUserIdAndCombatStatus(Long userId, CombatGeneralStatus combatStatus);
}