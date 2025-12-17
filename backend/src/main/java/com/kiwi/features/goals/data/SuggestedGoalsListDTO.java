package com.kiwi.features.goals.data;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class SuggestedGoalsListDTO {
    private List<SuggestedGoalDTO> goals;
}
