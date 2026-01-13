package com.kiwi.features.goals.data;

import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestedGoalDataMapper {

    public static SuggestedGoalPersistence toEntity(SuggestedGoalDTO dto, UsersPersistence user, LocalDate date) {
        return SuggestedGoalPersistence.builder()
                .id(dto.getId())
                .target(dto.getTarget())
                .action(dto.getAction())
                .type(GoalType.valueOf(dto.getType()))
                .category(GoalCategory.valueOf(dto.getCategory()))
                .reward(dto.getReward())
                .build();
    }

    public static SuggestedGoalDTO toDTO(SuggestedGoalPersistence goal) {
        return SuggestedGoalDTO.builder()
                .id(goal.getId())
                .target(goal.getTarget())
                .action(goal.getAction())
                .type(goal.getType().name())
                .category(goal.getCategory().name())
                .reward(goal.getReward())
                .build();
    }

    public static List<SuggestedGoalDTO> toListNewGoalDTO(List<SuggestedGoalPersistence> goals) {
        return goals.stream()
                .map(SuggestedGoalDataMapper::toDTO)
                .collect(Collectors.toList());
    }
}
