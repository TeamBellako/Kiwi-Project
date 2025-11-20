package com.kiwi.features.quests.data;

public class SubquestDomainFactory {

    public static SubquestDomain create(SubquestPersistence subquest, UserSubquestStatusPersistence subquestStatus) {
        return new SubquestDomain(
                subquest.getId(),
                subquest.getName(),
                subquest.getExperience(),
                subquest.getOrderIndex(),
                subquestStatus.getStatus()
        );
    }
}