package com.kiwi.goals;

import com.kiwi.features.goals.data.*;
import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;

public class GoalsTestFactory {

    // =========================================================================
    // GOAL PERSISTENCE (definitions - maps to goals table)
    // =========================================================================

    public static GoalPersistence goalDefinition(
            Long id,
            Long target,
            String action,
            String name,
            GoalType type,
            GoalCategory category,
            Integer reward
    ) {
        return GoalPersistence.builder()
                .id(id)
                .name(name)
                .action(action)
                .target(target)
                .type(type)
                .category(category)
                .reward(reward)
                .build();
    }

    public static GoalPersistence exerciseGoalDefinition(Long id) {
        return goalDefinition(id, 30L, "Exercise for 30 minutes", "Exercise Goal",
                GoalType.EXERCISE, GoalCategory.DAILY_CHALLENGES, 10);
    }

    public static GoalPersistence appGoalDefinition(Long id) {
        return goalDefinition(id, 100L, "Improve Java skills", "App Usage Goal",
                GoalType.PRODUCTIVITY, GoalCategory.APP_USAGE, 50);
    }

    public static GoalPersistence skillGoalDefinition(Long id) {
        return goalDefinition(id, 100L, "Improve Java skills", "Skill Goal",
                GoalType.PRODUCTIVITY, GoalCategory.SKILL, 50);
    }

    // =========================================================================
    // USER GOAL STATUS PERSISTENCE (per-user - maps to user_goal_status table)
    // =========================================================================

    public static UserGoalStatusPersistence userGoalStatusPersistence(
            Long id,
            GoalPersistence goal,
            GoalStatus status,
            LocalDate date,
            UsersPersistence user
    ) {
        return UserGoalStatusPersistence.builder()
                .id(id)
                .user(user)
                .goal(goal)
                .status(status)
                .date(date)
                .value(0L)
                .build();
    }

    public static UserGoalStatusPersistence inProgressGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, exerciseGoalDefinition(null), GoalStatus.IN_PROGRESS, date, user);
    }

    public static UserGoalStatusPersistence completedGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, exerciseGoalDefinition(null), GoalStatus.COMPLETED, date, user);
    }

    public static UserGoalStatusPersistence notCompletedGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, exerciseGoalDefinition(null), GoalStatus.NOT_COMPLETED, date, user);
    }

    public static UserGoalStatusPersistence appGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, appGoalDefinition(null), GoalStatus.IN_PROGRESS, date, user);
    }

    public static UserGoalStatusPersistence skillGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, skillGoalDefinition(null), GoalStatus.IN_PROGRESS, date, user);
    }

    // =========================================================================
    // USER GOAL STATUS DTO
    // =========================================================================

    public static UserGoalStatusDTO userGoalStatusDTO(
            Long id,
            Long goalId,
            String status,
            GoalType type,
            GoalCategory category,
            Integer reward
    ) {
        return UserGoalStatusDTO.builder()
                .id(id)
                .goalId(goalId)
                .name("Exercise Goal")
                .action("Exercise for 30 minutes")
                .target(30L)
                .type(type.name())
                .category(category.name())
                .reward(reward)
                .status(status)
                .value(0L)
                .build();
    }

    public static UserGoalStatusDTO inProgressGoalDTO(Long id) {
        return userGoalStatusDTO(id, 1L, "IN_PROGRESS",
                GoalType.EXERCISE, GoalCategory.DAILY_CHALLENGES, 10);
    }

    public static UserGoalStatusDTO completedGoalDTO(Long id) {
        return userGoalStatusDTO(id, 1L, "COMPLETED",
                GoalType.EXERCISE, GoalCategory.DAILY_CHALLENGES, 10);
    }

    public static UserGoalStatusDTO notCompletedGoalDTO(Long id) {
        return userGoalStatusDTO(id, 1L, "NOT_COMPLETED",
                GoalType.EXERCISE, GoalCategory.DAILY_CHALLENGES, 10);
    }

    public static UserGoalStatusDTO appGoalDTO(Long id) {
        return userGoalStatusDTO(id, 2L, "IN_PROGRESS",
                GoalType.PRODUCTIVITY, GoalCategory.APP_USAGE, 50);
    }

    public static UserGoalStatusDTO skillGoalDTO(Long id) {
        return userGoalStatusDTO(id, 3L, "IN_PROGRESS",
                GoalType.PRODUCTIVITY, GoalCategory.SKILL, 50);
    }
}

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

    public static GoalDTO skillGoalDTO(Long id) {
        return goalDTO(
                id,
                100L,
                "Improve Java skills",
                GoalType.PRODUCTIVITY,
                GoalCategory.SKILL,
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

    public static GoalPersistence skillGoalPersistence(
            Long id,
            LocalDate date,
            UsersPersistence user
    ) {
        return goalPersistence(
                id,
                100L,
                "Improve Java skills",
                GoalType.PRODUCTIVITY,
                GoalCategory.SKILL,
                GoalStatus.IN_PROGRESS,
                50,
                date,
                user
        );
    }
}
