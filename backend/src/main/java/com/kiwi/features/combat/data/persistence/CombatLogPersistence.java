package com.kiwi.features.combat.data.persistence;

import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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

    @Column(name="turn_number", nullable = false)
    private int turnNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="actor", nullable = false)
    private CombatActorType actor;

    @Enumerated(EnumType.STRING)
    @Column(name="action_type", nullable = false)
    private CombatActionType combatActionType;

    @Column(name="skill_id")
    private Long skillId;

    @Column(name="skill_name")
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(name="target")
    private CombatActorType target;

    @Enumerated(EnumType.STRING)
    @Column(name="effect_type")
    private SkillEffectResultType effectType;

    @Enumerated(EnumType.STRING)
    @Column(name="stat_affected")
    private StatType statAffected;

    private Float value;

    @Column(name="critic")
    private Boolean critic = false;

    @Column(name="state_id")
    private Long stateId;

    @Column(name="state_name")
    private String stateName;

    @Column(name="status_duration")
    private Integer statusDuration;

    @Column(name="blocked_skills")
    private String blockedSkills;  // separated by ','

    @Column(name="created_at")
    private Instant createdAt;
}