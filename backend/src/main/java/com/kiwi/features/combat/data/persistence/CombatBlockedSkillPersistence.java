package com.kiwi.features.combat.data.persistence;

import com.kiwi.features.combat.data.enums.CombatActorType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "combat_blocked_skills")
public class CombatBlockedSkillPersistence {

    @EmbeddedId
    private CombatBlockedSkillKey id;

    @MapsId("combatId")
    @Column(name = "combat_id")
    private Long combatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor")
    private CombatActorType actor;

    @Column(name = "skill_id")
    private Long skillId;
}