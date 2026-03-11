package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementMultiplierDTO {

    private Long elementId;

    private String elementName;

    private int elementIcon;

    private float multiplier;
}