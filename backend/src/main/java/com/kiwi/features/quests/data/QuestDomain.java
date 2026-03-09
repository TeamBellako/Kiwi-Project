package com.kiwi.features.quests.data;

import lombok.*;
import java.util.List;

@Getter
@Setter
public class QuestDomain {

    private int questId;
    private String name;
    private String description;
    private int experience;
    private int icon;
    private QuestStatus status;
    private List<SubquestDomain> subquests;
    private final String onCompletedEvent;
    private String onCompletedAction;
    private String onCompletedEntity;
    private final int onCompletedEntityId;

    public QuestDomain(int questId, String name, String description, int experience,
                int icon, QuestStatus status, List<SubquestDomain> subquests, String onCompletedAction, String onCompletedEntity, int onCompletedEntityId) {
        this.questId = questId;
        this.name = name;
        this.description = description;
        this.experience = experience;
        this.icon = icon;
        this.status = status;
        this.subquests = subquests;
        this.onCompletedEvent = onCompletedAction + "_" + onCompletedEntity;
        this.onCompletedAction = onCompletedAction;
        this.onCompletedEntity = onCompletedEntity;
        this.onCompletedEntityId = onCompletedEntityId;
    }
}
