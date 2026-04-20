package com.kiwi.features.skills.data.persistence;

import com.kiwi.features.skills.data.enums.SkillType;
import com.kiwi.features.skills.data.enums.CooldownType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "skills")
public class SkillPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SkillType skillType;
    
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    private String quote;

    @Enumerated(EnumType.STRING)
    @Column(name = "cooldown_type", nullable = false)
    private CooldownType cooldownType;

    @Column(name = "cooldown_goal_id")
    private Long cooldownGoalId;

    @Column(name = "cooldown_time_minutes")
    private Integer cooldownTimeMinutes;

    @Column(name = "cooldown_other_description")
    private String cooldownOtherDescription;

    @Column(name = "levelup_skill_id")
    private Long levelupSkillId;
}
