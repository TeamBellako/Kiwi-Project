package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatBlockedSkillPersistence;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatBlockedSkillRepository
        extends JpaRepository<CombatBlockedSkillPersistence, CombatBlockedSkillKey> {

    List<CombatBlockedSkillPersistence> findById_CombatId(Long combatId);

    void deleteByIdCombatId(Long combatId);
}