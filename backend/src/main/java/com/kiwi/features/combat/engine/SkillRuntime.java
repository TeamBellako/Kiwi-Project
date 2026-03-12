package com.kiwi.features.combat.engine;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class SkillRuntime {

    private Long id;

    private String name;

    private List<SkillEffectRuntime> effects;

}