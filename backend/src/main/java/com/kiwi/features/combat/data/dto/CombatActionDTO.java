package com.kiwi.features.combat.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatActionDTO {

    private String actor;     // USER / ENEMY
    private Long skillId;

    private List<CombatEffectDTO> effects;
}