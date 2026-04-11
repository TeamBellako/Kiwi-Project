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
@Table(name = "combat_active_status")
public class CombatActiveStatusPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="combat_id", nullable = false)
    private Long combatId;

    @Column(name="source_skill_id", nullable = false)
    private Long sourceSkillId;

    @Enumerated(EnumType.STRING)
    private CombatActorType target;

    @Column(name="state_id", nullable = false)
    private Long stateId;

    private Float value;

    @Column(name="remaining_turns", nullable = false)
    private int remainingTurns;
}