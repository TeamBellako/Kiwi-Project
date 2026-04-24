package com.kiwi.features.goals.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class GoalDTO {
    private Long id;
    private String name;
    private String action;
    private Integer target;
    private String type;
    private String category;
    private Integer reward;
    private String onCompletedAction;
    private String onCompletedEntity;
    private Integer onCompletedEntityId;
    private String onCompletedEvent;
}
