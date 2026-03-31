package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatStatusAppliedPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatStatusEffectRepository extends JpaRepository<CombatStatusAppliedPersistence, Long> {

    //TODO PORQUE ME LO HE INVENTAO
    List<CombatStatusAppliedPersistence> findByCombatIdAndTargetType(Long combatId, CombatActorType targetType);
}