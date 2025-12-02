// InMemoryTestDatabase.java
package com.kiwi.quests;

import com.kiwi.features.quests.data.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryTestDatabase {
    // Auto-increment simulados
    public final AtomicInteger questId = new AtomicInteger(1);
    public final AtomicInteger subquestId = new AtomicInteger(1);

    // Storage
    public final Map<Integer, QuestPersistence> quests = new HashMap<>();
    public final Map<Integer, SubquestPersistence> subquests = new HashMap<>();
    public final Map<UserQuestStatusKey, UserQuestStatusPersistence> userQuests = new HashMap<>();
    public final Map<UserSubquestStatusKey, UserSubquestStatusPersistence> userSubquests = new HashMap<>();

    // Métodos auxiliares para crear quests/subquests
    public QuestPersistence saveQuest(QuestPersistence quest) {
        if (quest.getId() == 0) quest.setId(questId.getAndIncrement());
        quests.put(quest.getId(), quest);
        return quest;
    }

    public SubquestPersistence saveSubquest(SubquestPersistence subquest) {
        if (subquest.getId() == 0) subquest.setId(subquestId.getAndIncrement());
        subquests.put(subquest.getId(), subquest);
        return subquest;
    }

    public UserQuestStatusPersistence saveUserQuest(UserQuestStatusPersistence uqs) {
        userQuests.put(uqs.getId(), uqs);
        return uqs;
    }

    public UserSubquestStatusPersistence saveUserSubquest(UserSubquestStatusPersistence uss) {
        userSubquests.put(uss.getId(), uss);
        return uss;
    }
}
