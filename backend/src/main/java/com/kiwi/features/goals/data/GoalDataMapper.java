package com.kiwi.features.goals.data;

import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GoalDataMapper {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static GoalPersistence toEntity(GoalDTO dto, UsersPersistence user, LocalDate date) {
        return GoalPersistence.builder()
                .id(dto.getId())
                .target(dto.getTarget())
                .action(dto.getAction())
                .type(GoalType.valueOf(dto.getType()))
                .category(GoalCategory.valueOf(dto.getCategory()))
                .status(GoalStatus.valueOf(dto.getStatus()))
                .reward(dto.getReward())
                .date(date)
                .value(dto.getValue())
                .user(user)
                .build();
    }

    public static GoalDTO toDTO(GoalPersistence goal) {
        return GoalDTO.builder()
                .id(goal.getId())
                .target(goal.getTarget())
                .action(goal.getAction())
                .type(goal.getType().name())
                .category(goal.getCategory().name())
                .status(goal.getStatus().name())
                .reward(goal.getReward())
                .date(goal.getDate().format(DATE_FORMATTER))
                .value(goal.getValue())
                .build();
    }
}
