package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatEffectDTO;
import com.kiwi.features.combat.data.enums.CombatActor;
import com.kiwi.features.combat.data.persistence.CombatPersistence;

import java.util.List;

public class CombatDamageCalculator {

    public CombatActionDTO executeSkill(
            CombatPersistence combat,
            CombatActor actor,
            Long skillId
    ) {

        float damage = calculateDamage(...);

        if(actor == CombatActor.USER) {
            combat.setEnemyHp(Math.max(0, combat.getEnemyHp() - (int)damage));
        } else {
            combat.setUserHp(Math.max(0, combat.getUserHp() - (int)damage));
        }

        CombatEffectDTO effect = CombatEffectDTO.builder()
                .type("DAMAGE")
                .target(actor == CombatActor.USER ? "ENEMY" : "USER")
                .value(damage)
                .crit(false)
                .build();

        return CombatActionDTO.builder()
                .actor(actor.name())
                .skillId(skillId)
                .effects(List.of(effect))
                .build();
    }
}


