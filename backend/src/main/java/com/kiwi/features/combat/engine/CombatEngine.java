package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatActorStateDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.CombatActor;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;

import java.util.ArrayList;
import java.util.List;

public class CombatEngine {

    private final CombatDamageCalculator damageCalculator;
    private final CombatStateService stateService;
    private final EnemyAI enemyAI;

    public CombatEngine(
            CombatDamageCalculator damageCalculator,
            CombatStateService stateService,
            EnemyAI enemyAI
    ) {
        this.damageCalculator = damageCalculator;
        this.stateService = stateService;
        this.enemyAI = enemyAI;
    }

    public CombatTurnResultDTO executeTurn(
            Long userId,
            CombatPersistence combat,
            Long skillId
    ) {

        List<CombatActionDTO> actions = new ArrayList<>();

        // ===============================
        // USER ACTION
        // ===============================

        CombatActionDTO userAction =
                damageCalculator.executeSkill(combat, CombatActor.USER, skillId);

        actions.add(userAction);

        // ===============================
        // ENEMY ACTION
        // ===============================

        Long enemySkill = enemyAI.chooseSkill(combat);

        CombatActionDTO enemyAction =
                damageCalculator.executeSkill(combat, CombatActor.ENEMY, enemySkill);

        actions.add(enemyAction);

        // ===============================
        // STATES
        // ===============================

        stateService.processTurnStates(combat);

        // ===============================
        // TURN UPDATE
        // ===============================

        combat.setTurnNumber(combat.getTurnNumber() + 1);

        // ===============================
        // CHECK COMBAT GENERAL STATUS
        // ===============================

        if(combat.getEnemyHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_WON);
        }

        if(combat.getUserHp() <= 0) {
            combat.setCombatStatus(CombatGeneralStatus.USER_LOST);
        }

        // ===============================
        // BUILD RESULT
        // ===============================

        CombatActorStateDTO userState =
                CombatActorStateDTO.builder()
                        .actor("USER")
                        .hp(combat.getUserHp())
                        .activeStates(stateService.getActiveStates(combat, CombatActor.USER))
                        .build();

        CombatActorStateDTO enemyState =
                CombatActorStateDTO.builder()
                        .actor("ENEMY")
                        .hp(combat.getEnemyHp())
                        .activeStates(stateService.getActiveStates(combat, CombatActor.ENEMY))
                        .build();

        return CombatTurnResultDTO.builder()
                .combatId(combat.getId())
                .turnNumber(combat.getTurnNumber())
                .user(userState)
                .enemy(enemyState)
                .actions(actions)
                .combatStatus(combat.getCombatStatus().name())
                .build();
    }
}