package com.kiwi.features.skills.data;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class SkillCombatDomain {

    private Long id;

    private String name;

    private List<SkillEffectDomain> effects;

}