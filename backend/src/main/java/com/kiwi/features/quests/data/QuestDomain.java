package com.kiwi.features.quests.data;

import lombok.*;
import java.util.List;

@Getter
@Setter
public class QuestDomain {

    private final Long questId;
    private final String name;
    private final String description;
    private final int experience;
    private final QuestStatus status;
    private final List<SubquestDomain> subquests;

    public QuestDomain(Long questId, String name, String description, int experience,
                       QuestStatus status, List<SubquestDomain> subquests) {
        this.questId = questId;
        this.name = name;
        this.description = description;
        this.experience = experience;
        this.status = status;
        this.subquests = subquests;
    }
}
