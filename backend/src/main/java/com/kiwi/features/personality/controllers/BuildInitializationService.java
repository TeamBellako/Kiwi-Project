package com.kiwi.features.personality.controllers;

import com.kiwi.features.combat.data.persistence.*;
import com.kiwi.features.combat.repositories.UserElementMultiplierRepository;
import com.kiwi.features.combat.repositories.UserStatsRepository;
import com.kiwi.features.combat.repositories.UserStatusResistanceRepository;
import com.kiwi.features.personality.data.BuildType;
import com.kiwi.features.skills.controllers.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BuildInitializationService {

    private final UserStatsRepository userStatsRepository;
    private final UserElementMultiplierRepository userElementMultiplierRepository;
    private final UserStatusResistanceRepository userStatusResistanceRepository;
    private final SkillService skillService;

    public BuildInitializationService(
            UserStatsRepository userStatsRepository,
            UserElementMultiplierRepository userElementMultiplierRepository,
            UserStatusResistanceRepository userStatusResistanceRepository,
            SkillService skillService
    ) {
        this.userStatsRepository = userStatsRepository;
        this.userElementMultiplierRepository = userElementMultiplierRepository;
        this.userStatusResistanceRepository = userStatusResistanceRepository;
        this.skillService = skillService;
    }

    @Transactional
    public void initializeIfAbsent(Long userId, BuildType build) {
        if (userStatsRepository.existsById(userId)) return;

        saveStats(userId, build);
        giveSkills(userId, build);
        saveElementMultipliers(userId, build);
        saveStatusResistances(userId, build);
    }

    private void saveStats(Long userId, BuildType build) {
        BuildType.BuildStats s = build.getStats();
        userStatsRepository.save(UserStatsPersistence.builder()
                .userId(userId)
                .maxHp(s.maxHp())
                .patk(s.patk())
                .matk(s.matk())
                .pdef(s.pdef())
                .mdef(s.mdef())
                .acc(s.acc())
                .eva(s.eva())
                .lck(s.lck())
                .build());
    }

    private void giveSkills(Long userId, BuildType build) {
        for (Long skillId : build.getSkillIds()) {
            skillService.giveSkillToUser(userId, skillId);
        }
    }

    @Transactional
    public void switchBuildSkills(Long userId, BuildType oldBuild, BuildType newBuild) {

        Set<Long> oldSkills = new HashSet<>(oldBuild.getSkillIds());
        Set<Long> newSkills = new HashSet<>(newBuild.getSkillIds());

        for (Long skillId : oldSkills) {
            if (!newSkills.contains(skillId)) {
                skillService.removeSkillFromUserIfPresent(userId, skillId);
            }
        }

        for (Long skillId : newBuild.getSkillIds()) {
            if (!skillService.userHasSkill(userId, skillId)) {
                skillService.giveSkillToUser(userId, skillId);
            }
        }
    }

    private void saveElementMultipliers(Long userId, BuildType build) {
        List<UserElementMultiplierPersistence> rows = build.getElementMultipliers().stream()
                .map(cfg -> UserElementMultiplierPersistence.builder()
                        .id(new UserElementalMultiplierKey(userId, cfg.elementId()))
                        .multiplier(cfg.multiplier())
                        .build())
                .toList();
        userElementMultiplierRepository.saveAll(rows);
    }

    private void saveStatusResistances(Long userId, BuildType build) {
        List<UserStatusResistancePersistence> rows = build.getStatusResistances().stream()
                .map(cfg -> UserStatusResistancePersistence.builder()
                        .id(new UserStatusResistanceKey(userId, cfg.stateId()))
                        .resistance(cfg.resistance())
                        .build())
                .toList();
        userStatusResistanceRepository.saveAll(rows);
    }
}
