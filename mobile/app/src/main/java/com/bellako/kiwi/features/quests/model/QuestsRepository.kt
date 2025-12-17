package com.bellako.kiwi.features.quests.model

import com.bellako.kiwi.features.quests.data.QuestDataMapper
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.SubquestResultDTO

class QuestsRepository(
    private val api: IQuestsAPI,
) {
    suspend fun getActiveQuests(): List<QuestDomain> = api.getActiveQuests().map { QuestDataMapper.toDomain(it) }

    suspend fun getCompletedQuests(): List<QuestDomain> = api.getCompletedQuests().map { QuestDataMapper.toDomain(it) }

    suspend fun giveQuest(questId: Int): QuestDomain = QuestDataMapper.toDomain(api.giveQuest(questId))

    suspend fun completeSubquest(subquestId: Int): SubquestResultDTO = api.completeSubquest(subquestId)

    suspend fun failSubquest(subquestId: Int): SubquestResultDTO = api.failSubquest(subquestId)
}
