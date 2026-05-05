package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.CombatDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatConfigPersistence;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.exceptions.CombatNotFoundException;
import com.kiwi.features.combat.repositories.CombatConfigRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CombatFacadeService {

    private final CombatService combatService;
    private final CombatBuilderService combatBuilderService;
    private final CombatTurnService combatTurnService;
    private final CombatConfigRepository combatConfigRepository;
    private final CombatBarkService combatBarkService;

    //------------------------------------------------------------------------------------------------------------------

    public CombatFacadeService(
            CombatService combatService,
            CombatBuilderService combatBuilderService,
            CombatTurnService combatTurnService,
            CombatConfigRepository combatConfigRepository,
            CombatBarkService combatBarkService
    ) {
        this.combatService = combatService;
        this.combatBuilderService = combatBuilderService;
        this.combatTurnService = combatTurnService;
        this.combatConfigRepository = combatConfigRepository;
        this.combatBarkService = combatBarkService;
    }

    //------------------------------------------------------------------------------------------------------------------

    // START / RESUME
    @Transactional
    public CombatDTO startOrResumeCombat(Long userId, Long configId) {

        CombatPersistence combat = combatService.startOrResume(userId, configId);

        return combatBuilderService.buildCombatDTO(combat);
    }

    //------------------------------------------------------------------------------------------------------------------

    // GET ACTIVE
    @Transactional(readOnly = true)
    public Optional<CombatDTO> getActiveCombat(Long userId) {

        return combatService.findActiveCombat(userId)
                .map(combatBuilderService::buildCombatDTO);
    }

    //------------------------------------------------------------------------------------------------------------------

    // EXECUTE TURN
    @Transactional
    public CombatTurnResultDTO executeTurn(Long userId, Long combatId, Long skillId) {

        CombatPersistence combat = combatService.findCombat(combatId)
                .orElseThrow(() -> new CombatNotFoundException(combatId));

        CombatTurnResultDTO result =
                combatTurnService.executeTurn(userId, combat, skillId);

        if (combat.getCombatStatus() == CombatGeneralStatus.USER_LOST) {
            combatService.resetStatsToOriginalConfig(combat);
        }

        if (combat.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            combatService.cleanDatabase(combatId);
        }

        return enrichWithConfig(result, combat.getCombatConfigId());
    }

    //------------------------------------------------------------------------------------------------------------------

    // TIMEOUT
    @Transactional
    public CombatTurnResultDTO timeOut(Long userId, Long combatId) {

        CombatPersistence combat = combatService.findCombat(combatId)
                .orElseThrow(() -> new CombatNotFoundException(combatId));

        CombatTurnResultDTO result =
                combatTurnService.handleTimeout(combat);

        if (combat.getCombatStatus() == CombatGeneralStatus.USER_LOST) {
            combatService.resetStatsToOriginalConfig(combat);
        }

        combatService.cleanDatabase(combat.getId());

        return enrichWithConfig(result, combat.getCombatConfigId());
    }

    //------------------------------------------------------------------------------------------------------------------

    // ABANDON
    @Transactional
    public CombatTurnResultDTO abandon(Long userId, Long combatId) {
        CombatPersistence combat = combatService.findCombat(combatId)
                .orElseThrow(() -> new CombatNotFoundException(combatId));

        CombatTurnResultDTO result =
                combatTurnService.handleAbandon(combat);

        combatService.resetStatsToOriginalConfig(combat);
        combatService.cleanDatabase(combat.getId());

        return enrichWithConfig(result, combat.getCombatConfigId());
    }

    //------------------------------------------------------------------------------------------------------------------

    // MARK BARK FIRED
    @Transactional
    public void markBarkFired(Long userId, Long combatId, Long triggerId) {

        CombatPersistence combat = combatService.findCombat(combatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!combat.getUserId().equals(userId)
                || combat.getCombatStatus() != CombatGeneralStatus.ONGOING
                || !combatBarkService.triggerBelongsToConfig(triggerId, combat.getCombatConfigId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        combatBarkService.markFired(combatId, triggerId);
    }

    //------------------------------------------------------------------------------------------------------------------

    private CombatTurnResultDTO enrichWithConfig(CombatTurnResultDTO result, Long configId) {
        CombatConfigPersistence config = combatConfigRepository.findById(configId).orElseThrow();
        result.setOnCompletedEvent(config.getOnCompletedAction() + '_' + config.getOnCompletedEntity());
        result.setOnCompletedEntityId(config.getOnCompletedEntityId());
        return result;
    }
}