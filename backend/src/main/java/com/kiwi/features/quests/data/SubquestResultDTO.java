package com.kiwi.features.quests.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class SubquestResultDTO {
    private SubquestDTO updatedSubquest;
    private SubquestDTO nextSubquest;      // null if updatedSubquest is the last subquest
    private QuestDTO completedQuest;       // null if updatedSubquest is not the last subquest
}
