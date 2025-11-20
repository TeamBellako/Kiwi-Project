package com.kiwi.features.quests.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubquestDomain {

    private final Long subquestId;
    private final String name;
    private final int experience;
    private final int order;
    private final SubquestStatus status;

    public SubquestDomain(Long subquestId, String name, int experience, int order, SubquestStatus status) {
        this.subquestId = subquestId;
        this.name = name;
        this.experience = experience;
        this.order = order;
        this.status = status;
    }
}
