package com.kiwi.features.goals.data;

import com.kiwi.features.users.data.UsersPersistence;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status;

    @Column(name = "reward", nullable = false)
    private Integer reward;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "\"value\"", nullable = false)
    private Long value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersPersistence user;
}
