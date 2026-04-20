package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "combat_last_skill")
public class CombatLastSkillPersistence {

    @EmbeddedId
    private CombatLastSkillKey id;

    @Column(name = "skill_id")
    private Long skillId;
}