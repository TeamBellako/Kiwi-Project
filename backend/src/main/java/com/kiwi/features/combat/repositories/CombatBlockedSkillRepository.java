package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillPersistence;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatBlockedSkillRepository
        extends JpaRepository<CombatBlockedSkillPersistence, CombatBlockedSkillKey> {

    List<Long> findSkillIdByIdCombatIdAndIdActor(Long combatId, CombatActorType actor);

    void deleteByIdCombatId(Long combatId);
}