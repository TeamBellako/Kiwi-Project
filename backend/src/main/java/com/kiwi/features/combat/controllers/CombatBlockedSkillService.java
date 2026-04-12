package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillKey;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillPersistence;
import com.kiwi.features.combat.repositories.CombatBlockedSkillRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombatBlockedSkillService {

    private final CombatBlockedSkillRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    public CombatBlockedSkillService(CombatBlockedSkillRepository repository) {

        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void syncBlockedSkills(
            Long combatId,
            List<Long> userSkillIds,
            List<Long> enemySkillIds
    ) {
        repository.deleteByIdCombatId(combatId);

        repository.saveAll(
                userSkillIds.stream()
                        .map(id -> new CombatBlockedSkillPersistence(
                                new CombatBlockedSkillKey(combatId, CombatActorType.USER, id)
                        ))
                        .toList()
        );

        repository.saveAll(
                enemySkillIds.stream()
                        .map(id -> new CombatBlockedSkillPersistence(
                                new CombatBlockedSkillKey(combatId, CombatActorType.ENEMY, id)
                        ))
                        .toList()
        );
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<Long> getBlockedSkills(Long combatId, CombatActorType actor) {

        return repository.findSkillIdByIdCombatIdAndIdActor(combatId, actor);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void deleteByCombatId(Long combatId) {

        repository.deleteByIdCombatId(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------

}