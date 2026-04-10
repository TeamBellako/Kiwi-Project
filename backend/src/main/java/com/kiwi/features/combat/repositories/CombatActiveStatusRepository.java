package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatActiveStatusPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatActiveStatusRepository extends JpaRepository<CombatActiveStatusPersistence, Long> {

    //TODO PORQUE ME LO HE INVENTAO
    List<CombatActiveStatusPersistence> findByCombatIdAndTargetType(Long combatId, CombatActorType targetType);
}