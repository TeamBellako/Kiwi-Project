package com.kiwi.features.quests.data;

import java.util.List;
import java.util.stream.Collectors;

public class QuestDomainFactory {

    public static QuestDomain create(QuestPersistence quest, UserQuestStatusPersistence questStatus,  List<SubquestDomain> subquests) {
        return new QuestDomain(
                quest.getId(),
                quest.getName(),
                quest.getDescription(),
                quest.getExperience(),
                quest.getIcon(),
                questStatus.getStatus(),
                subquests.stream()
                        .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
                        .collect(Collectors.toList()),
                "",
                "",
                0
        );
    }
}
