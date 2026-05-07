package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.domain.CombatTurnResultDomain;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.mappers.CombatMapper;
import com.kiwi.features.combat.data.mappers.CombatTurnResultMapper;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.engine.CombatContext;
import com.kiwi.features.combat.engine.CombatEngine;
import com.kiwi.features.combat.exceptions.CombatFinishedException;
import com.kiwi.features.combat.exceptions.CombatNotFoundException;
import com.kiwi.features.combat.exceptions.NotTimedCombatException;
import com.kiwi.features.skills.controllers.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;

@Service
public class CombatTurnService {

    private final CombatEngine combatEngine;
    private final CombatLogService combatLogService;
    private final CombatLastSkillService lastSkillService;
    private final CombatBlockedSkillService blockedSkillService;
    private final CombatProgressService combatProgressService;
    private final SkillService skillService;
    private final CombatActorBuilderService combatActorBuilderService;

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnService(
            CombatEngine combatEngine,
            CombatLogService combatLogService,
            CombatLastSkillService lastSkillService,
            CombatBlockedSkillService blockedSkillService,
            CombatProgressService combatProgressService,
            SkillService skillService, CombatActorBuilderService combatActorBuilderService
    ) {
        this.combatEngine = combatEngine;
        this.combatLogService = combatLogService;
        this.lastSkillService = lastSkillService;
        this.blockedSkillService = blockedSkillService;
        this.combatProgressService = combatProgressService;
        this.skillService = skillService;
        this.combatActorBuilderService = combatActorBuilderService;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO executeTurn(Long userId, CombatPersistence combat, Long skillId) {

        skillService.putSkillOnCooldown(userId, skillId);

        Map<CombatActorType, CombatActorDomain> actors =
                combatActorBuilderService.buildActors(combat);

        CombatActorDomain user = actors.get(CombatActorType.USER);
        CombatActorDomain enemy = actors.get(CombatActorType.ENEMY);

        CombatContext context = new CombatContext(
                CombatMapper.toDomain(combat),
                user,
                enemy
        );

        CombatTurnResultDomain result = combatEngine.executeTurn(context, skillId);

        combatLogService.saveCombatActions(result.getActions(), combat.getId(), combat.getTurnNumber());

        lastSkillService.updateLastSkills(
                combat.getId(),
                context.getUser().getLastSkillUsed(),
                context.getEnemy().getLastSkillUsed()
        );

        blockedSkillService.syncBlockedSkills(
                combat.getId(),
                context.getUser().getBlockedSkills(),
                context.getEnemy().getBlockedSkills()
        );

        combatProgressService.applyTurnResult(
                combat,
                CombatMapper.toDomain(combat),
                context.getUser(),
                context.getEnemy()
        );

        for (Long resetCooldownId : context.getUser().getResetCooldownSkills())
        {
            skillService.removeCooldown(userId, resetCooldownId);
        }

        return CombatTurnResultMapper.toDTO(result);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO handleTimeout(CombatPersistence combat) {

        if (combat.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new CombatFinishedException(combat.getId());
        }

        if (combat.getEndsAt() == null) {
            throw new NotTimedCombatException(combat.getId());
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

            return CombatTurnResultMapper.toDTO(combatEngine.buildTimeoutCombatTurnResult(combatDomain));
        }

        return new CombatTurnResultDTO();
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDTO handleAbandon(CombatPersistence combat) {

        if (combat.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new CombatFinishedException(combat.getId());
        }

        CombatDomain combatDomain = CombatMapper.toDomain(combat);

        combatProgressService.updateAbandon(combatDomain);

        combatProgressService.applyTurnResult(
                combat,
                combatDomain,
                null,
                null
        );

        return CombatTurnResultMapper.toDTO(combatEngine.buildAbandonCombatTurnResult(combatDomain));

    }

    //------------------------------------------------------------------------------------------------------------------

}