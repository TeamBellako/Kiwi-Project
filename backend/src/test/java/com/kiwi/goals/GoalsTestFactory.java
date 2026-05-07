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
            Integer target,
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
            .difficulty(1)
                .build();
    }

    public static GoalPersistence exerciseGoalDefinition(Long id) {
        return goalDefinition(id, 30, "Exercise for 30 minutes", "Exercise Goal",
                GoalType.EXERCISE, GoalCategory.DAILY_CHALLENGES, 10);
    }

    public static GoalPersistence appGoalDefinition(Long id) {
        return goalDefinition(id, 100, "Improve Java skills", "App Usage Goal",
                GoalType.PRODUCTIVITY, GoalCategory.APP_USAGE, 50);
    }

    public static GoalPersistence skillGoalDefinition(Long id) {
        return goalDefinition(id, 100, "Improve Java skills", "Skill Goal",
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
                .value(0)
                .build();
    }

    public static UserGoalStatusPersistence inProgressGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, exerciseGoalDefinition(1L), GoalStatus.IN_PROGRESS, date, user);
    }

    public static UserGoalStatusPersistence completedGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, exerciseGoalDefinition(1L), GoalStatus.COMPLETED, date, user);
    }

    public static UserGoalStatusPersistence notCompletedGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, exerciseGoalDefinition(1L), GoalStatus.NOT_COMPLETED, date, user);
    }

    public static UserGoalStatusPersistence appGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, appGoalDefinition(2L), GoalStatus.IN_PROGRESS, date, user);
    }

    public static UserGoalStatusPersistence skillGoalPersistence(Long id, LocalDate date, UsersPersistence user) {
        return userGoalStatusPersistence(id, skillGoalDefinition(3L), GoalStatus.IN_PROGRESS, date, user);
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
                .target(30)
                .type(type.name())
                .category(category.name())
                .reward(reward)
                .status(status)
                .value(0)
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
