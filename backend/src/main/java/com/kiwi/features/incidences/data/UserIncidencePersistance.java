package com.kiwi.features.incidences.data;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user_incidences")
public class UserIncidencePersistance {
    @EmbeddedId
    private UserIncidenceKey id;

    @Column(nullable = false)
    private boolean value = false;
}
