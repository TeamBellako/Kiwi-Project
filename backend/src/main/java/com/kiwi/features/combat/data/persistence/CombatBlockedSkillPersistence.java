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
}