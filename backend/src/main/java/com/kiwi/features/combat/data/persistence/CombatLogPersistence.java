package com.kiwi.features.combat.data.persistence;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.SkillEffectResultType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

//TODO ESTA CLASE HAY QUE REVISARLA AL FINAL PARA VER COMO METEMOS TODA LA INFO EN LA BBDD
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "combat_log")
public class CombatLogPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="combat_id", nullable = false)
    private Long combatId;

    @Column(name="turn_number")
    private int turnNumber;

    @Enumerated(EnumType.STRING)
    private CombatActorType actor;

    @Column(name="skill_id")
    private Long skillId;

    @Enumerated(EnumType.STRING)
    private CombatActorType target;

    @Enumerated(EnumType.STRING)
    @Column(name="effect_type")
    private SkillEffectResultType effectType;

    private Float value;

    @Column(name="state_id")
    private Long stateId;

    @Column(name="status_duration")
    private Integer statusDuration;

    @Column(name="created_at")
    private Instant createdAt;
}