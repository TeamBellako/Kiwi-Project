package com.kiwi.features.goals.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UserGoalStatusDTO {
    private Long id;
    private Long goalId;
    // Denormalized from GoalPersistence for display
    private String name;
    private String action;
    private Integer target;
    private String type;
    private String category;
    private Integer reward;
    private Integer onCompletedEntityId;
    private String onCompletedEvent;
    // Per-user status fields
    private String status;
    private String date;
    private Integer value;
}
