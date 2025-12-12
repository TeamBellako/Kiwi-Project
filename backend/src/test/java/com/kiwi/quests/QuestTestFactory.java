package com.kiwi.quests;

import com.kiwi.features.quests.data.*;

public class QuestTestFactory {

    // =========================================================================
    // QUESTS
    // =========================================================================

    public static QuestPersistence quest(int id) {
        QuestPersistence q = new QuestPersistence();
        q.setName("Quest"+id);
        q.setDescription("Description of "+id);
        q.setExperience(100+id);
        return q;
    }

    // =========================================================================
    // SUBQUESTS
    // =========================================================================

    public static SubquestPersistence subquest(int id, QuestPersistence quest, int orderIndex) {
        SubquestPersistence s = new SubquestPersistence();
        s.setQuest(quest);
        s.setOrderIndex(orderIndex);
        s.setName("Sub" + id);
        s.setExperience(500+id);
        return s;
    }

    // =========================================================================
    // QUEST STATUS
    // =========================================================================

    public static UserQuestStatusPersistence activeQuestStatus(Long userId, QuestPersistence quest) {
        return questStatus(userId, quest, QuestStatus.ACTIVE);
    }

    public static UserQuestStatusPersistence completedQuestStatus(Long userId, QuestPersistence quest) {
        return questStatus(userId, quest, QuestStatus.COMPLETED);
    }

    private static UserQuestStatusPersistence questStatus(Long userId, QuestPersistence quest, QuestStatus status) {
        UserQuestStatusPersistence p = new UserQuestStatusPersistence();
        p.setId(new UserQuestStatusKey(userId, quest.getId()));
        p.setStatus(status);
        p.setQuest(quest);
        return p;
    }

    // =========================================================================
    // SUBQUEST STATUS
    // =========================================================================

    public static UserSubquestStatusPersistence activeSubquestStatus(Long userId, SubquestPersistence subquest) {
        return subquestStatus(userId, subquest, SubquestStatus.ACTIVE);
    }

    public static UserSubquestStatusPersistence lockedSubquestStatus(Long userId, SubquestPersistence subquest) {
        return subquestStatus(userId, subquest, SubquestStatus.LOCKED);
    }

    public static UserSubquestStatusPersistence completedSubquestStatus(Long userId, SubquestPersistence subquest) {
        return subquestStatus(userId, subquest, SubquestStatus.COMPLETED);
    }

    public static UserSubquestStatusPersistence failedSubquestStatus(Long userId, SubquestPersistence subquest) {
        return subquestStatus(userId, subquest, SubquestStatus.FAILED);
    }

    private static UserSubquestStatusPersistence subquestStatus(Long userId, SubquestPersistence subquest, SubquestStatus status) {
        UserSubquestStatusPersistence p = new UserSubquestStatusPersistence();
        p.setId(new UserSubquestStatusKey(userId, subquest.getId()));
        p.setStatus(status);
        p.setSubquest(subquest);
        return p;
    }
}
