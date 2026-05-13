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
import com.kiwi.features.combat.engine.CombatEngine;
import com.kiwi.features.combat.exceptions.CombatFinishedException;
import com.kiwi.features.combat.exceptions.NotTimedCombatException;
import com.kiwi.features.skills.controllers.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CombatTurnResultDTO executeTurn(Long userId, CombatPersistence combatPersistence, Long skillId) {

        skillService.putSkillOnCooldown(userId, skillId);

        Map<CombatActorType, CombatActorDomain> actors =
                combatActorBuilderService.buildActors(combatPersistence);

        CombatActorDomain user = actors.get(CombatActorType.USER);
        CombatActorDomain enemy = actors.get(CombatActorType.ENEMY);

        CombatDomain combatDomain = new CombatDomain(combatPersistence.getId(), combatPersistence.getCombatConfigId(), user, enemy,
                combatPersistence.getTurnNumber(), combatPersistence.getCombatStatus(), combatPersistence.getEndsAt());

        CombatTurnResultDomain result = combatEngine.executeTurn(combatDomain, skillId);

        combatLogService.saveCombatActions(result.getActions(), combatPersistence.getId(), combatPersistence.getTurnNumber());

        lastSkillService.updateLastSkills(
                combatPersistence.getId(),
                combatDomain.getUser().getLastSkillUsed(),
                combatDomain.getEnemy().getLastSkillUsed()
        );

        blockedSkillService.syncBlockedSkills(
                combatPersistence.getId(),
                combatDomain.getUser().getBlockedSkills(),
                combatDomain.getEnemy().getBlockedSkills()
        );

        combatProgressService.applyTurnResult(combatPersistence,combatDomain);

        for (Long resetCooldownId : combatDomain.getUser().getResetCooldownSkills())
        {
            skillService.removeCooldown(userId, resetCooldownId);
        }

        return CombatTurnResultMapper.toDTO(result);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO handleTimeout(CombatPersistence combatPersistence) {

        if (combatPersistence.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new CombatFinishedException(combatPersistence.getId());
        }

        if (combatPersistence.getEndsAt() == null) {
            throw new NotTimedCombatException(combatPersistence.getId());
        }

        CombatDomain combatDomain = new CombatDomain(combatPersistence.getId(), combatPersistence.getCombatConfigId(), null, null,
                combatPersistence.getTurnNumber(), combatPersistence.getCombatStatus(), combatPersistence.getEndsAt());

        combatProgressService.updateTimeOut(combatDomain);

        if (combatDomain.getCombatStatus() != CombatGeneralStatus.ONGOING) {

            combatProgressService.applyTurnResult(combatPersistence, combatDomain);

            return CombatTurnResultMapper.toDTO(combatEngine.buildTimeoutCombatTurnResult(combatDomain));
        }

        return new CombatTurnResultDTO();
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatTurnResultDTO handleAbandon(CombatPersistence combatPersistence) {

        if (combatPersistence.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new CombatFinishedException(combatPersistence.getId());
        }

        CombatDomain combatDomain = new CombatDomain(combatPersistence.getId(), combatPersistence.getCombatConfigId(), null, null,
                combatPersistence.getTurnNumber(), combatPersistence.getCombatStatus(), combatPersistence.getEndsAt());

        combatProgressService.updateAbandon(combatDomain);

        combatProgressService.applyTurnResult(combatPersistence,combatDomain);

        return CombatTurnResultMapper.toDTO(combatEngine.buildAbandonCombatTurnResult(combatDomain));

    }

    //------------------------------------------------------------------------------------------------------------------

}