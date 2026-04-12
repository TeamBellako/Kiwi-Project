package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.ElementMultiplierDomain;
import com.kiwi.features.combat.data.domain.StatusResistanceDomain;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.mappers.*;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.repositories.EnemyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CombatBuilderService {

    private final CombatActorBuilderService actorBuilderService;
    private final CombatLogService combatLogService;
    private final EnemyRepository enemyRepository;

    public CombatBuilderService(
            CombatActorBuilderService actorBuilderService,
            CombatLogService combatLogService,
            EnemyRepository enemyRepository
    ) {
        this.actorBuilderService = actorBuilderService;
        this.combatLogService = combatLogService;
        this.enemyRepository = enemyRepository;
    }

    public CombatDTO buildCombatDTO(CombatPersistence combat) {

        CombatActorDomain user = actorBuilderService.buildUser(combat.getUserId(), combat);
        CombatActorDomain enemy = actorBuilderService.buildEnemy(combat.getEnemyId(), combat);

        CombatActorDTO userDTO = mapActorToDTO(user);
        CombatActorDTO enemyDTO = mapActorToDTO(enemy);

        EnemyPersistence enemyEntity =
                enemyRepository.findById(combat.getEnemyId()).orElseThrow();

        List<CombatActionDTO> log =
                combatLogService.getCombatLog(combat.getId());

        return CombatMapper.toDTO(
                combat,
                userDTO,
                enemyDTO,
                enemyEntity.getName(),
                enemyEntity.getSprite(),
                log
        );
    }

    // -------------------------------------------------------------------------------------

    private CombatActorDTO mapActorToDTO(CombatActorDomain actor) {

        return CombatActorMapper.toDTO(
                actor.getHp(),
                StatsMapper.toDTO(actor),
                ElementMultiplierMapper.toDTOList(
                        new ArrayList<>(actor.getElementMultipliers().values())
                ),
                StatusResistanceMapper.toDTOList(
                        new ArrayList<>(actor.getStatusResistances().values())
                ),
                CombatActiveStatusMapper.toDTOList(actor.getStates())
        );
    }
}