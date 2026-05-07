package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "combat_fired_barks")
public class CombatFiredBarkPersistence {

    @EmbeddedId
    private CombatFiredBarkKey id;
}
