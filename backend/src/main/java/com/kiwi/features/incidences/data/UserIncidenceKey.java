package com.kiwi.features.incidences.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class UserIncidenceKey implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "incidence_id")
    private Long incidenceId;
}
