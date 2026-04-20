package com.kiwi.features.quests.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class SubquestDTO {
    private int subquestId;
    private String name;
    private int experience;
    private int order;
    private String status;
    private String onCompletedEvent;
    private int onCompletedEntityId;
}
