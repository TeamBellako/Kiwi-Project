package com.kiwi.features.goals.data;

import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestedGoalDataMapper {

    public static SuggestedGoalPersistence toEntity(SuggestedGoalDTO dto, UsersPersistence user, LocalDate date) {
        return SuggestedGoalPersistence.builder()
                .id(dto.getId())
                .objective(dto.getObjective())
                .description(dto.getDescription())
                .type(GoalType.valueOf(dto.getType()))
                .category(GoalCategory.valueOf(dto.getCategory()))
                .status(GoalStatus.valueOf(dto.getStatus()))
                .points(dto.getPoints())
                .build();
    }

    public static SuggestedGoalDTO toDTO(SuggestedGoalPersistence goal) {
        return SuggestedGoalDTO.builder()
                .id(goal.getId())
                .objective(goal.getObjective())
                .description(goal.getDescription())
                .type(goal.getType().name())
                .category(goal.getCategory().name())
                .status(goal.getStatus().name())
                .points(goal.getPoints())
                .build();
    }

    public static List<SuggestedGoalDTO> toListNewGoalDTO(List<SuggestedGoalPersistence> goals) {
        return goals.stream()
                .map(SuggestedGoalDataMapper::toDTO)
                .collect(Collectors.toList());
    }
}
