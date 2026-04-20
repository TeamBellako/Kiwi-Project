package com.kiwi.features.skills.data.persistence;

import com.kiwi.features.combat.data.enums.AttackType;
import com.kiwi.features.combat.data.enums.StatModificationType;
import com.kiwi.features.combat.data.enums.StatType;
import com.kiwi.features.skills.data.enums.SkillEffectTargetType;
import com.kiwi.features.skills.data.enums.SkillEffectType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "skill_effects")
public class SkillEffectPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="skill_id", nullable = false)
    private Long skillId;

    @Enumerated(EnumType.STRING)
    @Column(name="target", nullable = false)
    private SkillEffectTargetType target;

    @Enumerated(EnumType.STRING)
    @Column(name="effect_type", nullable = false)
    private SkillEffectType effectType;

    @Enumerated(EnumType.STRING)
    @Column(name="stat_affected")
    private StatType statAffected;

    @Enumerated(EnumType.STRING)
    @Column(name="stat_modification")
    private StatModificationType statModification;

    private Float power;

    @Enumerated(EnumType.STRING)
    @Column(name="attack_type")
    private AttackType attackType;

    @Column(name="element_id")
    private Long elementId;

    @Column(name="hit_chance")
    private Integer hitChance;

    @Column(name="state_id")
    private Long stateId;

    @Column(name="status_duration")
    private Integer statusDuration;
}