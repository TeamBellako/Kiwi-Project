package com.kiwi.features.quests.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubquestDomain {

    private final int subquestId;
    private final String name;
    private final int experience;
    private final int order;
    private final SubquestStatus status;
    private final String onCompletedEvent;
    private final int onCompletedEntityId;

    public SubquestDomain(int subquestId, String name, int experience, int order, SubquestStatus status, String onCompletedEvent, int onCompletedEntityId) {
        this.subquestId = subquestId;
        this.name = name;
        this.experience = experience;
        this.order = order;
        this.status = status;
        this.onCompletedEvent = onCompletedEvent;
        this.onCompletedEntityId = onCompletedEntityId;
    }
}
