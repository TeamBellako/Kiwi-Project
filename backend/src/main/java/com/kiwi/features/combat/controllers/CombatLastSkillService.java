package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatLastSkillKey;
import com.kiwi.features.combat.data.persistence.CombatLastSkillPersistence;
import com.kiwi.features.combat.repositories.CombatLastSkillRepository;
import org.springframework.stereotype.Component;

@Component
public class CombatLastSkillService {

    private final CombatLastSkillRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    public CombatLastSkillService(CombatLastSkillRepository repository) {
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void updateLastSkills(Long combatId, Long userSkill, Long enemySkill) {

        if (userSkill != null && userSkill != -1) {
            repository.save(
                    CombatLastSkillPersistence.builder()
                            .id(new CombatLastSkillKey(combatId, CombatActorType.USER))
                            .skillId(userSkill)
                            .build()
            );
        }

        if (enemySkill != null && enemySkill != -1) {
            repository.save(
                    CombatLastSkillPersistence.builder()
                            .id(new CombatLastSkillKey(combatId, CombatActorType.ENEMY))
                            .skillId(enemySkill)
                            .build()
            );
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public Long getLastSkill(Long combatId, CombatActorType actor) {
        return repository
                .findSkillIdByIdCombatIdAndIdActor(combatId, actor)
                .orElse(-1L);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void deleteByCombatId(Long combatId) {
        repository.deleteByIdCombatId(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------

}