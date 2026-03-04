package com.kiwi.features.goals.data;

public class GoalDataMapper {

    public static GoalPersistence toEntity(GoalDTO dto) {
        return GoalPersistence.builder()
                .id(dto.getId())
                .name(dto.getName())
                .action(dto.getAction())
                .target(dto.getTarget())
                .type(GoalType.valueOf(dto.getType()))
                .category(GoalCategory.valueOf(dto.getCategory()))
                .reward(dto.getReward())
                .build();
    }

    public static GoalDTO toDTO(GoalPersistence goal) {
        return GoalDTO.builder()
                .id(goal.getId())
                .name(goal.getName())
                .action(goal.getAction())
                .target(goal.getTarget())
                .type(goal.getType().name())
                .category(goal.getCategory().name())
                .reward(goal.getReward())
                .build();
    }
}
