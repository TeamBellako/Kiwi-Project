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
    private Long target;
    private String action;
    private String type;
    private String category;
    private String status;
    private Integer reward;
    private String date;
    private Long value;
}
