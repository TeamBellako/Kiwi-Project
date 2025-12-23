package com.kiwi.features.quests.data;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class QuestDTO {
    private int questId;
    private String name;
    private String description;
    private int experience;
    private int icon;
    private String status;
    private List<SubquestDTO> subquests;
}