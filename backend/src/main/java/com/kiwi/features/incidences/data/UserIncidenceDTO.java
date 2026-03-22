package com.kiwi.features.incidences.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode @ToString
public class UserIncidenceDTO {
    private String name;
    private boolean value;
}
