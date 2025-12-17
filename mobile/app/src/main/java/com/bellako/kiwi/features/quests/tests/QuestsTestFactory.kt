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

    private fun questWithThreeSubquests(): QuestDomain =
        QuestDomain(
            id = 1,
            name = "First Quest",
            description = "Complete your first challenge",
            experience = 100,
            status = QuestStatus.ACTIVE,
            subquests =
                listOf(
                    subquest(1, "Objective 1", SubquestStatus.COMPLETED),
                    subquest(2, "Objective 2", SubquestStatus.COMPLETED),
                    subquest(3, "Objective 3", SubquestStatus.ACTIVE),
                ),
        )

    private fun questWithFourSubquests(): QuestDomain =
        QuestDomain(
            id = 2,
            name = "Second Quest",
            description = "A longer mission with multiple steps",
            experience = 200,
            status = QuestStatus.ACTIVE,
            subquests =
                listOf(
                    subquest(4, "Objective 1", SubquestStatus.COMPLETED),
                    subquest(5, "Objective 2", SubquestStatus.COMPLETED),
                    subquest(6, "Objective 3", SubquestStatus.ACTIVE),
                    subquest(7, "Objective 4", SubquestStatus.LOCKED),
                ),
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
        )
}
