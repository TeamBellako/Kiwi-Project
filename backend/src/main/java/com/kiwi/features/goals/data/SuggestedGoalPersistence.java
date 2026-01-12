package com.kiwi.features.goals.data;

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
@Table(name = "suggested_goals")
public class SuggestedGoalPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "target", nullable = false)
    private Long target;

    @Column(name = "action", columnDefinition = "TEXT")
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GoalType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private GoalCategory category;

    @Column(name = "reward", nullable = false)
    private Integer reward;
}
