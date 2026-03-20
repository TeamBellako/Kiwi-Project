package com.kiwi.features.incidences.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode @ToString
public class IncidenceDTO {
    private String name;
    private boolean value;
}
