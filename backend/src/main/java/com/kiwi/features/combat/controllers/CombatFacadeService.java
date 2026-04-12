package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.CombatDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.exceptions.CombatNotFoundException;
import com.kiwi.features.combat.repositories.CombatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CombatFacadeService {

    private final CombatService combatService;
    private final CombatBuilderService combatBuilderService;
    private final CombatTurnService combatTurnService;

    private final CombatRepository combatRepository;

    //------------------------------------------------------------------------------------------------------------------

    public CombatFacadeService(
            CombatService combatService,
            CombatBuilderService combatBuilderService,
            CombatTurnService combatTurnService,
            CombatRepository combatRepository
    ) {
        this.combatService = combatService;
        this.combatBuilderService = combatBuilderService;
        this.combatTurnService = combatTurnService;
        this.combatRepository = combatRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    // START / RESUME
    public CombatDTO startOrResumeCombat(Long userId, Long configId) {

        CombatPersistence combat = combatService.startOrCreate(userId, configId);

        return combatBuilderService.buildCombatDTO(combat);
    }

    //------------------------------------------------------------------------------------------------------------------

    // EXECUTE TURN
    @Transactional
    public CombatTurnResultDTO executeTurn(Long userId, Long combatId, Long skillId) {

        CombatPersistence combat = combatRepository.findById(combatId)
                .orElseThrow(() -> new CombatNotFoundException(combatId));

        CombatTurnResultDTO result =
                combatTurnService.executeTurn(userId, combat, skillId);

        if (combat.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            combatService.cleanDatabase(combatId);
        }

        return result;
    }

    //------------------------------------------------------------------------------------------------------------------

    // TIMEOUT
    public CombatTurnResultDTO timeOut(Long userId, Long combatId) {

        CombatPersistence combat = combatRepository.findById(combatId)
                .orElseThrow(() -> new CombatNotFoundException(combatId));

        CombatTurnResultDTO result =
                combatTurnService.handleTimeout(userId, combat);

        combatService.cleanDatabase(combat.getId());

        return result;
    }
}