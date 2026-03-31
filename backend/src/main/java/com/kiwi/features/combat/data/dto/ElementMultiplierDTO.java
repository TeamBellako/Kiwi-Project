package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementMultiplierDTO {

    private Long elementId;

    private String name;

    private int icon;

    private String description;

    private float multiplier;
}