package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.enums.CombatActor;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Builder
@Getter
public class CombatContext {

    private Random random = new Random();

    private CombatPersistence combat;

    private ActorRuntime user;
    private ActorRuntime enemy;

    private List<CombatActionDTO> actions = new ArrayList<>();

    public CombatContext(
            CombatPersistence combat,
            ActorRuntime user,
            ActorRuntime enemy
    ) {
        this.combat = combat;
        this.user = user;
        this.enemy = enemy;
    }

    public ActorRuntime getActor(CombatActor actor) {
        return actor == CombatActor.USER ? user : enemy;
    }

    public ActorRuntime getTarget(CombatActor actor) {
        return actor == CombatActor.USER ? enemy : user;
    }

    public void addAction(CombatActionDTO action) {
        actions.add(action);
    }

}