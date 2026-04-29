package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.mappers.*;
import com.kiwi.features.combat.data.persistence.CombatConfigPersistence;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.repositories.CombatConfigRepository;
import com.kiwi.features.combat.repositories.EnemyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CombatBuilderService {


    private final EnemyRepository enemyRepository;
    private final CombatLogService combatLogService;
    private final CombatActorBuilderService combatActorBuilderService;
    private final CombatConfigRepository combatConfigRepository;

    public CombatBuilderService(
            CombatLogService combatLogService,
            EnemyRepository enemyRepository,
            CombatActorBuilderService combatActorBuilderService,
            CombatConfigRepository combatConfigRepository
    ) {
        this.combatLogService = combatLogService;
        this.enemyRepository = enemyRepository;
        this.combatActorBuilderService = combatActorBuilderService;
        this.combatConfigRepository = combatConfigRepository;
    }

    public CombatDTO buildCombatDTO(CombatPersistence combat) {

        Map<CombatActorType, CombatActorDomain> actors =
                combatActorBuilderService.buildActors(combat);

        CombatActorDomain user = actors.get(CombatActorType.USER);
        CombatActorDomain enemy = actors.get(CombatActorType.ENEMY);

        CombatActorDTO userDTO = mapActorToDTO(user);
        CombatActorDTO enemyDTO = mapActorToDTO(enemy);

        EnemyPersistence enemyEntity =
                enemyRepository.findById(combat.getEnemyId()).orElseThrow();

        CombatConfigPersistence config =
                combatConfigRepository.findById(combat.getCombatConfigId()).orElseThrow();

        Long backgroundId = config.getBackground() != null
                ? config.getBackground().getId()
                : null;

        Long sfxId = config.getSfx() != null
                ? config.getSfx().getId()
                : null;

        List<CombatActionDTO> log =
                combatLogService.getCombatLog(combat.getId());

        return CombatMapper.toDTO(
                combat,
                userDTO,
                enemyDTO,
                enemyEntity.getName(),
                enemyEntity.getSprite(),
                backgroundId,
                sfxId,
                log
        );
    }

    // -------------------------------------------------------------------------------------

    private CombatActorDTO mapActorToDTO(CombatActorDomain actor) {

        return CombatActorMapper.toDTO(
                StatsMapper.toDTO(actor.getStats()),
                ElementMultiplierMapper.toDTOList(
                        new ArrayList<>(actor.getElementMultipliers().values())
                ),
                StatusResistanceMapper.toDTOList(
                        new ArrayList<>(actor.getStatusResistances().values())
                ),
                CombatActiveStatusMapper.toDTOList(actor.getActiveStatuses())
        );
    }

}