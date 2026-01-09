package com.kiwi.goals;

import com.kiwi.features.goals.data.*;
import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;

public class GoalsTestFactory {

    // =========================================================================
    // GOAL PERSISTENCE
    // =========================================================================

    public static GoalPersistence goalPersistence(
            Long id,
            Long objective,
            String description,
            GoalType type,
            GoalCategory category,
            GoalStatus status,
            Integer points,
            LocalDate date,
            UsersPersistence user
    ) {
        return GoalPersistence.builder()
                .id(id)
                .objective(objective)
                .description(description)
                .type(type)
                .category(category)
                .status(status)
                .points(points)
                .date(date)
                .user(user)
                .build();
    }

    // =========================================================================
    // GOAL DTO
    // =========================================================================

    public static GoalDTO goalDTO(
            Long id,
            Long objective,
            String description,
            GoalType type,
            GoalCategory category,
            GoalStatus status,
            Integer points
    ) {
        return GoalDTO.builder()
                .id(id)
                .objective(objective)
                .description(description)
                .type(type.name())
                .category(category.name())
                .status(status.name())
                .points(points)
                .build();
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    public static GoalDTO inProgressGoalDTO(Long id) {
        return goalDTO(
                id,
                30L,
                "Exercise for 30 minutes",
                GoalType.EXERCISE,
                GoalCategory.DAILY_CHALLENGES,
                GoalStatus.IN_PROGRESS,
                10
        );
    }

    public static GoalDTO completedGoalDTO(Long id) {
        return goalDTO(
                id,
                30L,
                "Exercise for 30 minutes",
                GoalType.EXERCISE,
                GoalCategory.DAILY_CHALLENGES,
                GoalStatus.COMPLETED,
                10
        );
    }

    public static GoalDTO notCompletedGoalDTO(Long id) {
        return goalDTO(
                id,
                30L,
                "Exercise for 30 minutes",
                GoalType.EXERCISE,
                GoalCategory.DAILY_CHALLENGES,
                GoalStatus.NOT_COMPLETED,
                10
        );
    }

    public static GoalPersistence inProgressGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return goalPersistence(
                id,
                30L,
                "Exercise for 30 minutes",
                GoalType.EXERCISE,
                GoalCategory.DAILY_CHALLENGES,
                GoalStatus.IN_PROGRESS,
                10,
                date,
                user
        );
    }

    public static GoalPersistence completedGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return goalPersistence(
                id,
                30L,
                "Exercise for 30 minutes",
                GoalType.EXERCISE,
                GoalCategory.DAILY_CHALLENGES,
                GoalStatus.COMPLETED,
                10,
                date,
                user
        );
    }

    public static GoalPersistence notCompletedGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return goalPersistence(
                id,
                30L,
                "Exercise for 30 minutes",
                GoalType.EXERCISE,
                GoalCategory.DAILY_CHALLENGES,
                GoalStatus.NOT_COMPLETED,
                10,
                date,
                user
        );
    }
}
