package com.kiwi.features.goals.data;

import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class GoalDataMapper {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static GoalPersistence toEntity(GoalDTO dto, UsersPersistence user, LocalDate date) {
        return GoalPersistence.builder()
                .id(dto.getId())
                .objective(dto.getObjective())
                .category(GoalCategory.valueOf(dto.getCategory()))
                .status(GoalStatus.valueOf(dto.getStatus()))
                .points(dto.getPoints())
                .date(date)
                .user(user)
                .build();
    }

    public static GoalDTO toDTO(GoalPersistence goal) {
        return GoalDTO.builder()
                .id(goal.getId())
                .objective(goal.getObjective())
                .category(goal.getCategory().name())
                .status(goal.getStatus().name())
                .points(goal.getPoints())
                .build();
    }

    public static GoalsListDTO toGoalsListDTO(LocalDate date, List<GoalPersistence> goals) {
        List<GoalDTO> goalDTOs = goals.stream()
                .map(GoalDataMapper::toDTO)
                .collect(Collectors.toList());

        return GoalsListDTO.builder()
                .date(date.format(DATE_FORMATTER))
                .goals(goalDTOs)
                .build();
    }
}
