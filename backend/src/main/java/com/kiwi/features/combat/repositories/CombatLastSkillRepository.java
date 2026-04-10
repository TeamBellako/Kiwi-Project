package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatLastSkillPersistence;
import com.kiwi.features.combat.data.persistence.CombatLastSkillKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CombatLastSkillRepository
        extends JpaRepository<CombatLastSkillPersistence, CombatLastSkillKey> {

    Optional<Long> findSkillIdByIdCombatIdAndIdActor(Long combatId, CombatActorType actor);

    void deleteByIdCombatId(Long combatId);
}