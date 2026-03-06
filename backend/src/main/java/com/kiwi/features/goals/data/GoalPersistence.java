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
@Table(name = "goals")
public class GoalPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "action", columnDefinition = "TEXT")
    private String action;

    @Column(name = "target", nullable = false)
    private Integer target;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GoalType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private GoalCategory category;

    @Column(name = "reward", nullable = false)
    private Integer reward;
}
