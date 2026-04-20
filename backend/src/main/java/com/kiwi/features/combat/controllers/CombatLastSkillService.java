package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatLastSkillKey;
import com.kiwi.features.combat.data.persistence.CombatLastSkillPersistence;
import com.kiwi.features.combat.repositories.CombatLastSkillRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<CombatActorType, Long> getLastSkills(Long combatId) {
        return repository.findById_CombatId(combatId)
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getId().getActor(),
                        CombatLastSkillPersistence::getSkillId
                ));
    }

    //------------------------------------------------------------------------------------------------------------------

    public Long getLastSkillForActor(
            Map<CombatActorType, Long> lastSkillsUsed,
            CombatActorType combatActorType
    ) {
        if (lastSkillsUsed == null || combatActorType == null) {
            return -1L;
        }

        return lastSkillsUsed.getOrDefault(combatActorType, -1L);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void deleteByCombatId(Long combatId) {

        repository.deleteByIdCombatId(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------

}