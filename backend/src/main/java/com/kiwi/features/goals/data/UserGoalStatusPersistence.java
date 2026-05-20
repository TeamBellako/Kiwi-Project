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
@Table(name = "user_goal_status")
public class UserGoalStatusPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersPersistence user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private GoalPersistence goal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "value", nullable = false)
    private Integer value;

    /**
     * Per-instance target override for goals whose target is computed dynamically
     * (e.g. APP_USAGE_GOOD / APP_USAGE_BAD). When null, the target from the goal
     * template ({@link GoalPersistence#getTarget()}) is used instead.
     */
    @Column(name = "target_override")
    private Integer targetOverride;
}
