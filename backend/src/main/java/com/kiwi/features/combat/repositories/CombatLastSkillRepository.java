package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatLastSkillPersistence;
import com.kiwi.features.combat.data.persistence.CombatLastSkillKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CombatLastSkillRepository
        extends JpaRepository<CombatLastSkillPersistence, CombatLastSkillKey> {

    List<CombatLastSkillPersistence> findById_CombatId(Long combatId);

    void deleteByIdCombatId(Long combatId);
}