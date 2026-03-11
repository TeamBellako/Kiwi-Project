package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatElementDTO {

    private Long id;
    private String name;
    private int icon;
    private String description;
}