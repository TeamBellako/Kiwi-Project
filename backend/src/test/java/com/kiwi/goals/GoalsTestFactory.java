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
            Long target,
            String action,
            GoalType type,
            GoalCategory category,
            GoalStatus status,
            Integer reward,
            LocalDate date,
            UsersPersistence user
    ) {
        return GoalPersistence.builder()
                .id(id)
                .target(target)
                .action(action)
                .type(type)
                .category(category)
                .status(status)
                .reward(reward)
                .date(date)
                .value(0L)
                .user(user)
                .build();
    }

    // =========================================================================
    // GOAL DTO
    // =========================================================================

    public static GoalDTO goalDTO(
            Long id,
            Long target,
            String action,
            GoalType type,
            GoalCategory category,
            GoalStatus status,
            Integer reward
    ) {
        return GoalDTO.builder()
                .id(id)
                .target(target)
                .action(action)
                .type(type.name())
                .category(category.name())
                .status(status.name())
                .reward(reward)
                .value(0L)
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

    public static GoalDTO appGoalDTO(Long id) {
        return goalDTO(
                id,
                100L,
                "Improve Java skills",
                GoalType.PRODUCTIVITY,
                GoalCategory.APP_USAGE,
                GoalStatus.IN_PROGRESS,
                50
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

    public static GoalPersistence appGoalPersistence(
            Long id,
            LocalDate date,
            UsersPersistence user
    ) {
        return goalPersistence(
                id,
                100L,
                "Improve Java skills",
                GoalType.PRODUCTIVITY,
                GoalCategory.APP_USAGE,
                GoalStatus.IN_PROGRESS,
                50,
                date,
                user
        );
    }
}
