package com.bellako.kiwi.features.quests.tests

import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestStatus
import com.bellako.kiwi.features.quests.data.QuestsState
import com.bellako.kiwi.features.quests.data.SubquestDomain
import com.bellako.kiwi.features.quests.data.SubquestStatus

@Suppress("MagicNumber")
object QuestsTestFactory {
    fun validQuestsState(): QuestsState =
        QuestsState(
            quests =
                listOf(
                    questWithThreeSubquests(),
                    questWithFourSubquests(),
                ),
        )

    fun questWithThreeSubquests(): QuestDomain =
        QuestDomain(
            id = 1,
            name = "First Quest",
            description = "Complete your first challenge",
            experience = 100,
            icon = 1,
            status = QuestStatus.ACTIVE,
            subquests =
                listOf(
                    subquest(1, "Objective 1", SubquestStatus.COMPLETED),
                    subquest(2, "Objective 2", SubquestStatus.COMPLETED),
                    subquest(3, "Objective 3", SubquestStatus.ACTIVE),
                ),
            "_",
            0,
        )

    fun questWithFourSubquests(): QuestDomain =
        QuestDomain(
            id = 2,
            name = "Second Quest",
            description = "A longer mission with multiple steps",
            experience = 200,
            icon = 2,
            status = QuestStatus.ACTIVE,
            subquests =
                listOf(
                    subquest(4, "Objective 1", SubquestStatus.COMPLETED),
                    subquest(5, "Objective 2", SubquestStatus.COMPLETED),
                    subquest(6, "Objective 3", SubquestStatus.ACTIVE),
                    subquest(7, "Objective 4", SubquestStatus.LOCKED),
                ),
            "_",
            0,
        )

    fun questCompleted(): QuestDomain =
        QuestDomain(
            id = 2,
            name = "Third Quest",
            description = "A short mission",
            experience = 200,
            icon = 2,
            status = QuestStatus.COMPLETED,
            subquests =
                listOf(
                    subquest(8, "Objective 1", SubquestStatus.COMPLETED),
                ),
            "_",
            0,
        )

    fun newQuest(): QuestDomain =
        QuestDomain(
            id = 2,
            name = "Fourth Quest",
            description = "A short mission",
            experience = 200,
            icon = 2,
            status = QuestStatus.ACTIVE,
            subquests =
                listOf(
                    subquest(9, "Objective 1", SubquestStatus.LOCKED),
                    subquest(10, "Objective 2", SubquestStatus.LOCKED),
                    subquest(11, "Objective 3", SubquestStatus.LOCKED),
                ),
            "_",
            0,
        )

    private fun subquest(
        id: Int,
        name: String,
        status: SubquestStatus,
    ): SubquestDomain =
        SubquestDomain(
            id = id,
            name = name,
            experience = 50,
            order = id,
            status = status,
            onCompletedEvent = "_",
            onCompletedEntityId = 0,
        )
}
