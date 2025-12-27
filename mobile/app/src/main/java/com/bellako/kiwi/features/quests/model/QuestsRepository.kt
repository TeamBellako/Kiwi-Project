package com.bellako.kiwi.features.quests.model

import com.bellako.kiwi.features.quests.data.QuestDTO
import com.bellako.kiwi.features.quests.data.QuestDataMapper
import com.bellako.kiwi.features.quests.data.QuestDomain

class QuestsRepository(
    private val api: IQuestsAPI,
) {
    suspend fun getActiveQuests(): List<QuestDomain> = api.getActiveQuests().map { QuestDataMapper.toDomain(it) }

    suspend fun getCompletedQuests(): List<QuestDomain> = api.getCompletedQuests().map { QuestDataMapper.toDomain(it) }

    suspend fun giveQuest(questId: Int): QuestDomain = QuestDataMapper.toDomain(api.giveQuest(questId))

    suspend fun completeSubquest(subquestId: Int): QuestDTO = api.completeSubquest(subquestId)

    suspend fun failSubquest(subquestId: Int): QuestDTO = api.failSubquest(subquestId)
}
