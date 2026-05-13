package com.kiwi.features.goals.data;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user_goal_progress")
public class UserGoalProgressPersistence {

    @EmbeddedId
    private UserGoalProgressKey id;

    @Column(name = "current_difficulty", nullable = false)
    private Integer currentDifficulty;

    @Column(name = "goals_completed_at_difficulty", nullable = false)
    private Integer goalsCompletedAtDifficulty;

    @Column(name = "goals_failed_at_difficulty", nullable = false)
    private Integer goalsFailedAtDifficulty;
}