package com.kiwi.features.goals.data;

import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserGoalStatusDataMapper {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static UserGoalStatusPersistence toEntity(
            UserGoalStatusDTO dto,
            UsersPersistence user,
            GoalPersistence goal,
            LocalDate date) {
        return UserGoalStatusPersistence.builder()
                .id(dto.getId())
                .user(user)
                .goal(goal)
                .status(GoalStatus.valueOf(dto.getStatus()))
                .date(date)
                .value(dto.getValue())
                .build();
    }

    public static UserGoalStatusDTO toDTO(UserGoalStatusPersistence entity) {
        GoalPersistence goal = entity.getGoal();
        return UserGoalStatusDTO.builder()
                .id(entity.getId())
                .goalId(goal.getId())
                .name(goal.getName())
                .action(goal.getAction())
                .target(goal.getTarget())
                .type(goal.getType().name())
                .category(goal.getCategory().name())
                .reward(goal.getReward())
                .status(entity.getStatus().name())
                .date(entity.getDate().format(DATE_FORMATTER))
                .value(entity.getValue())
                .build();
    }
}
