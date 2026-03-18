package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.enums.CombatActorType;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class SkillRuntime {

    private Long id;
    private String name;
    private CombatActorType target;
    private List<SkillEffectRuntime> effects;

}