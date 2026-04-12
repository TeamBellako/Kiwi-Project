package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.mappers.CombatMapper;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.engine.CombatContext;
import com.kiwi.features.combat.engine.CombatEngine;
import com.kiwi.features.skills.controllers.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CombatTurnService {

    private final CombatEngine combatEngine;
    private final CombatLogService combatLogService;
    private final CombatLastSkillService lastSkillService;
    private final CombatBlockedSkillService blockedSkillService;
    private final CombatProgressService combatProgressService;
    private final SkillService skillService;

    private final CombatActorBuilderService actorBuilderService;

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnService(
            CombatEngine combatEngine,
            CombatLogService combatLogService,
            CombatLastSkillService lastSkillService,
            CombatBlockedSkillService blockedSkillService,
            CombatProgressService combatProgressService,
            SkillService skillService,
            CombatActorBuilderService actorBuilderService
    ) {
        this.combatEngine = combatEngine;
        this.combatLogService = combatLogService;
        this.lastSkillService = lastSkillService;
        this.blockedSkillService = blockedSkillService;
        this.combatProgressService = combatProgressService;
        this.skillService = skillService;
        this.actorBuilderService = actorBuilderService;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO executeTurn(Long userId, CombatPersistence combat, Long skillId) {

        skillService.putSkillOnCooldown(userId, skillId);

        CombatActorDomain user = actorBuilderService.buildUser(userId, combat);
        CombatActorDomain enemy = actorBuilderService.buildEnemy(combat.getEnemyId(), combat);

        CombatContext context = new CombatContext(
                CombatMapper.toDomain(combat),
                user,
                enemy
        );

        CombatTurnResultDTO result = combatEngine.executeTurn(context, skillId);

        combatLogService.saveCombatActions(result.getActions(), combat.getId(), combat.getTurnNumber());

        lastSkillService.updateLastSkills(
                combat.getId(),
                context.getUser().getLastSkillUsed(),
                context.getEnemy().getLastSkillUsed()
        );

        blockedSkillService.syncBlockedSkills(
                combat.getId(),
                new ArrayList<>(context.getUser().getSkills().keySet()),
                new ArrayList<>(context.getEnemy().getSkills().keySet())
        );

        combatProgressService.applyTurnResult(
                combat,
                CombatMapper.toDomain(combat),
                context.getUser(),
                context.getEnemy()
        );

        return result;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO handleTimeout(Long userId, CombatPersistence combat) {

        if (combat.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new IllegalStateException("Combat already finished");
        }

        if (combat.getEndsAt() == null) {
            throw new IllegalStateException("No timed combat");
        }

        CombatDomain combatDomain = CombatMapper.toDomain(combat);

        combatProgressService.updateTimeOut(combatDomain);

        if (combatDomain.getCombatStatus() != CombatGeneralStatus.ONGOING) {

            combatProgressService.applyTurnResult(
                    combat,
                    combatDomain,
                    null,
                    null
            );

            return combatEngine.buildTimeoutCombatTurnResultDTO(userId, combatDomain);
        }

        return new CombatTurnResultDTO();
    }

    //------------------------------------------------------------------------------------------------------------------

}