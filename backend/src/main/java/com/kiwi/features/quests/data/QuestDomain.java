package com.kiwi.features.quests.data;

import lombok.*;
import java.util.List;

@Getter
@Setter
public class QuestDomain {

    private final int questId;
    private final String name;
    private final String description;
    private final int experience;
    private final int icon;
    private final QuestStatus status;
    private final List<SubquestDomain> subquests;

    public QuestDomain(int questId, String name, String description, int experience,
                int icon, QuestStatus status, List<SubquestDomain> subquests) {
        this.questId = questId;
        this.name = name;
        this.description = description;
        this.experience = experience;
        this.icon = icon;
        this.status = status;
        this.subquests = subquests;
    }
}
