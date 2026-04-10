package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.enums.ActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.mappers.CombatActionMapper;
import com.kiwi.features.combat.data.persistence.CombatBlockedSkillPersistence;
import com.kiwi.features.combat.data.persistence.CombatLastSkillKey;
import com.kiwi.features.combat.data.persistence.CombatLastSkillPersistence;
import com.kiwi.features.combat.data.persistence.CombatLogPersistence;
import com.kiwi.features.combat.repositories.CombatBlockedSkillRepository;
import com.kiwi.features.combat.repositories.CombatLastSkillRepository;
import com.kiwi.features.combat.repositories.CombatLogRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CombatLogService {

    private final CombatLogRepository combatLogRepository;
    private final CombatLastSkillRepository combatLastSkillRepository;
    private final CombatBlockedSkillRepository combatBlockedSkillRepository;

    public CombatLogService(CombatLogRepository combatLogRepository, CombatLastSkillRepository combatLastSkillRepository,
                            CombatBlockedSkillRepository combatBlockedSkillRepository) {
        this.combatLogRepository = combatLogRepository;
        this.combatLastSkillRepository = combatLastSkillRepository;
        this.combatBlockedSkillRepository = combatBlockedSkillRepository;
    }

    public List<CombatActionDTO> getCombatLog(Long combatId){

        List<CombatActionDTO> actions = new ArrayList<>();
        List<CombatLogPersistence> actionsPersistence = combatLogRepository.findByCombatIdOrderByIdAsc(combatId);

        for (CombatLogPersistence combatLogPersistence : actionsPersistence) {
            CombatActionDTO action = CombatActionMapper.toDTO(combatLogPersistence);
            actions.add(action);
        }

        return actions;
    }


    @Transactional
    public void saveCombatActions(List<CombatActionDTO> actions, Long combatId, int turnNumber) {

        List<CombatLogPersistence> logPersistenceAction = actions.stream()
                .map(a -> CombatLogPersistence.builder()
                        .combatId(combatId)
                        .turnNumber(turnNumber)
                        .actor(CombatActorType.valueOf(a.getActor()))
                        .actionType(ActionType.valueOf(a.getActionType()))
                        .skillName(a.getSkillName())
                        .stateId(a.getStateName())
                        .value(a.getValue() != null ? a.getValue() : null)
                        .blockedSkills(a.getBlockedSkills() != null
                                ? a.getBlockedSkills().toString()
                                : null)
                        .createdAt(Instant.now())
                        .build()
                )
                .toList();

        combatLogRepository.saveAll(logPersistenceAction);
    }

    void deleteCombatLog(Long combatId){
        combatLogRepository.deleteByCombatId(combatId);
    }


    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void updateLastSkills(Long combatId, Long userLastSkillUsed, Long enemyLastSkillUsed) {

        if (userLastSkillUsed != null && userLastSkillUsed != -1) {
            CombatLastSkillPersistence userLastSkillPersistence =
                    CombatLastSkillPersistence.builder()
                            .id(new CombatLastSkillKey(combatId, CombatActorType.USER))
                            .skillId(userLastSkillUsed)
                            .build();

            combatLastSkillRepository.save(userLastSkillPersistence);
        }

        if (enemyLastSkillUsed != null && enemyLastSkillUsed != -1) {
            CombatLastSkillPersistence enemyLastSkillPersistence =
                    CombatLastSkillPersistence.builder()
                            .id(new CombatLastSkillKey(combatId, CombatActorType.ENEMY))
                            .skillId(enemyLastSkillUsed)
                            .build();

            combatLastSkillRepository.save(enemyLastSkillPersistence);
        }
    }

    public Long getLastSkillUsed (Long combatId, CombatActorType actor){

        Optional<Long> skillId = combatLastSkillRepository.findSkillIdByIdCombatIdAndIdActor(combatId, actor);

        return skillId.orElse(-1L);
    }

    void deleteLastSkillsUsed(Long combatId){
        combatLogRepository.deleteByCombatId(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void syncBlockedSkills(Long combatId, List<Long> userSkillIds, List<Long> enemySkillIds) {

        deleteCombatLog(combatId);

        List<CombatBlockedSkillPersistence> userBlockedSkillsPersistence = userSkillIds.stream()
                .map(id -> new CombatBlockedSkillPersistence(combatId, CombatActorType.USER, id))
                .toList();

        List<CombatBlockedSkillPersistence> enemyBlockedSkillsPersistence = enemySkillIds.stream()
                .map(id -> new CombatBlockedSkillPersistence(combatId, CombatActorType.ENEMY, id))
                .toList();

        combatBlockedSkillRepository.saveAll();
    }

    public List<Long> getSkillsBlocked(Long combatId, CombatActorType actor){

        return combatBlockedSkillRepository.findSkillIdByIdCombatIdAndIdActor(combatId, actor);
    }

    void deleteSkillsBlocked(Long combatId){
        combatLogRepository.deleteByCombatId(combatId);
    }


}