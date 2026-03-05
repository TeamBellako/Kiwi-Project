package com.kiwi.features.quests.controllers;

import com.kiwi.features.quests.data.*;
import org.springframework.stereotype.Service;

@Service
public class QuestProgressService {

    // --------------------------------------------------------------------------------------------
    // QUEST
    // --------------------------------------------------------------------------------------------

    public QuestDomain activateQuest(QuestDomain quest) {
        if (quest.getStatus() != null) {
            throw new IllegalStateException("Quest already started for this user");
        }
        return new QuestDomain(
                quest.getQuestId(),
                quest.getName(),
                quest.getDescription(),
                quest.getExperience(),
                quest.getIcon(),
                QuestStatus.ACTIVE,
                quest.getSubquests(),
                quest.getOnCompletedEvent(),
                quest.getOnCompletedEntityId()
        );
    }

    public QuestDomain completeQuest(QuestDomain quest) {
        if (quest.getStatus() != QuestStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE quests can be completed");
        }
        return new QuestDomain(
                quest.getQuestId(),
                quest.getName(),
                quest.getDescription(),
                quest.getExperience(),
                quest.getIcon(),
                QuestStatus.COMPLETED,
                quest.getSubquests(),
                quest.getOnCompletedEvent(),
                quest.getOnCompletedEntityId()
        );
    }


    // --------------------------------------------------------------------------------------------
    // SUBQUESTS
    // --------------------------------------------------------------------------------------------

    public SubquestDomain unlockSubquest(SubquestDomain subquest) {
        if (subquest.getStatus() != SubquestStatus.LOCKED) {
            throw new IllegalStateException("Only LOCKED subquests can be unlocked");
        }
        return new SubquestDomain(
                subquest.getSubquestId(),
                subquest.getName(),
                subquest.getExperience(),
                subquest.getOrder(),
                SubquestStatus.ACTIVE,
                subquest.getOnCompletedEvent(),
                subquest.getOnCompletedEntityId()
        );
    }

    public SubquestDomain completeSubquest(SubquestDomain subquest) {
        if (subquest.getStatus() != SubquestStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE subquests can be completed");
        }
        return new SubquestDomain(
                subquest.getSubquestId(),
                subquest.getName(),
                subquest.getExperience(),
                subquest.getOrder(),
                SubquestStatus.COMPLETED,
                subquest.getOnCompletedEvent(),
                subquest.getOnCompletedEntityId()
        );
    }

    public SubquestDomain failSubquest(SubquestDomain subquest) {
        if (subquest.getStatus() != SubquestStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE subquests can be failed");
        }
        return new SubquestDomain(
                subquest.getSubquestId(),
                subquest.getName(),
                subquest.getExperience(),
                subquest.getOrder(),
                SubquestStatus.FAILED,
                subquest.getOnCompletedEvent(),
                subquest.getOnCompletedEntityId()
        );
    }
}
