package com.kiwi.features.quests.data;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class QuestDTO {
    private Long questId;
    private String name;
    private String description;
    private int experience;
    private String status;
    private List<SubquestDTO> subquests;
}
