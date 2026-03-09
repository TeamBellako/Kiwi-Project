package com.kiwi.features.combat.data.state;

import com.kiwi.features.combat.data.CombatActor;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "combat_status_effects")
public class CombatStatusEffectPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="combat_id", nullable = false)
    private Long combatId;

    @Column(name="source_skill_id")
    private Long sourceSkillId;

    @Enumerated(EnumType.STRING)
    private CombatActor target;

    @Column(name="state_id")
    private Long stateId;

    private Float value;

    @Column(name="remaining_turns")
    private int remainingTurns;
}