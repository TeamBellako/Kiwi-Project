package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.enums.ActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.mappers.CombatActionMapper;
import com.kiwi.features.combat.data.persistence.*;
import com.kiwi.features.combat.repositories.CombatBlockedSkillRepository;
import com.kiwi.features.combat.repositories.CombatLastSkillRepository;
import com.kiwi.features.combat.repositories.CombatLogRepository;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
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

    void deleteCombatLog(Long combatId){

        combatLogRepository.deleteByCombatId(combatId);
    }


    @Transactional
    public void saveCombatActions(List<CombatActionDTO> actions, Long combatId, int turnNumber) {
        List<CombatLogPersistence> logs = actions.stream()
                .flatMap(a -> mapAction(a, combatId, turnNumber).stream())
                .toList();

        combatLogRepository.saveAll(logs);
    }


    private List<CombatLogPersistence> mapAction(
            CombatActionDTO action,
            Long combatId,
            int turnNumber
    ) {

        ActionType type = ActionType.valueOf(action.getActionType());

        return switch (type) {

            case SKILL_USED -> mapSkillUsed(action, combatId, turnNumber);

            case ACTOR_BLOCKED_BY_STATE,
                 SKILL_REPEAT_BY_STATE,
                 STATUS_TURN_REDUCED,
                 STATUS_FINISHED -> List.of(
                    baseBuilder(action, combatId, turnNumber)
                            .stateName(action.getStateName())
                            .stateId(action.getStateId())
                            .build()
            );

            case ACTOR_DAMAGED_BY_STATE -> List.of(
                    baseBuilder(action, combatId, turnNumber)
                            .stateName(action.getStateName())
                            .stateId(action.getStateId())
                            .value(action.getValue())
                            .build()
            );

            case BLOCKED_SKILLS_BY_STATE,
                 RELEASED_SKILLS_BY_STATE -> List.of(
                    baseBuilder(action, combatId, turnNumber)
                            .stateName(action.getStateName())
                            .stateId(action.getStateId())
                            .blockedSkills(CombatActionMapper.blockedSkillsToString(action.getBlockedSkills()))
                            .build()
            );

            case SKIP, TIMEOUT -> List.of(
                    baseBuilder(action, combatId, turnNumber).build()
            );
        };
    }

    private CombatLogPersistence.CombatLogPersistenceBuilder baseBuilder(
            CombatActionDTO action,
            Long combatId,
            int turnNumber
    ) {
        return CombatLogPersistence.builder()
                .combatId(combatId)
                .turnNumber(turnNumber)
                .actor(CombatActorType.valueOf(action.getActor()))
                .actionType(ActionType.valueOf(action.getActionType()))
                .createdAt(Instant.now());
    }

    private List<CombatLogPersistence> mapSkillUsed(
            CombatActionDTO action,
            Long combatId,
            int turnNumber
    ) {

        if (action.getEffects() == null || action.getEffects().isEmpty()) {
            return List.of(
                    baseBuilder(action, combatId, turnNumber)
                            .skillName(action.getSkillName())
                            .build()
            );
        }

        return action.getEffects().stream()
                .map(effect -> {

                    CombatLogPersistence.CombatLogPersistenceBuilder builder =
                            baseBuilder(action, combatId, turnNumber)
                                    .skillName(action.getSkillName())
                                    .effectType(SkillEffectResultType.valueOf(effect.getTypeResult()))
                                    .target(CombatActorType.valueOf(effect.getTarget()))
                                    .value(effect.getValue())
                                    .critic(effect.isCritic());

                    if (effect.getAppliedStatus() != null) {
                        builder
                                .stateId(effect.getAppliedStatus().getStateId())
                                .statusDuration(effect.getAppliedStatus().getRemainingTurns());
                    }

                    return builder.build();
                })
                .toList();
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
                .map(id ->
                        CombatBlockedSkillPersistence.builder()
                                .id(new CombatBlockedSkillKey(combatId, CombatActorType.USER, id))
                                .build())
                .toList();

        List<CombatBlockedSkillPersistence> enemyBlockedSkillsPersistence = enemySkillIds.stream()
                .map(id ->
                        CombatBlockedSkillPersistence.builder()
                                .id(new CombatBlockedSkillKey(combatId, CombatActorType.ENEMY, id))
                                .build())
                .toList();

        combatBlockedSkillRepository.saveAll(userBlockedSkillsPersistence);
        combatBlockedSkillRepository.saveAll(enemyBlockedSkillsPersistence);
    }

    public List<Long> getSkillsBlocked(Long combatId, CombatActorType actor){

        return combatBlockedSkillRepository.findSkillIdByIdCombatIdAndIdActor(combatId, actor);
    }

    void deleteSkillsBlocked(Long combatId){

        combatLogRepository.deleteByCombatId(combatId);
    }

}