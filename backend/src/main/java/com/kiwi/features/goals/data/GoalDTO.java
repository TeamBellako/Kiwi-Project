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
    private Long objective;
    private String description;
    private String type;
    private String category;
    private String status;
    private Integer points;
    private String date;
}
