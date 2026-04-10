package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_elemental_multipliers")
public class UserElementMultiplierPersistence {

    @EmbeddedId
    private UserElementalMultiplierKey id;

    @Column(name = "multiplier", nullable = false)
    private Float multiplier;
}