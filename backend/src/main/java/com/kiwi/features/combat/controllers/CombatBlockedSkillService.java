package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillKey;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillPersistence;
import com.kiwi.features.combat.repositories.CombatBlockedSkillRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<CombatActorType, List<Long>> getBlockedSkills(Long combatId) {

        List<CombatBlockedSkillPersistence> list =
                repository.findById_CombatId(combatId);

        return list.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getId().getActor(),
                        Collectors.mapping(
                                e -> e.getId().getSkillId(),
                                Collectors.toList()
                        )
                ));
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<Long> getBlockedSkillsForActor(
            Map<CombatActorType, List<Long>> skillsBlocked,
            CombatActorType combatActorType
    ) {
        if (skillsBlocked == null || combatActorType == null) {
            return List.of();
        }

        return skillsBlocked.getOrDefault(combatActorType, List.of());
    }


    //------------------------------------------------------------------------------------------------------------------

    public void deleteByCombatId(Long combatId) {

        repository.deleteByIdCombatId(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------

}