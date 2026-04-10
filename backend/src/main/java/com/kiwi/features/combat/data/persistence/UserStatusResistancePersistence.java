package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_status_resistances")
public class UserStatusResistancePersistence {

    @EmbeddedId
    private UserStatusResistanceKey id;

    @Column(name = "resistance", nullable = false)
    private Float resistance;
}