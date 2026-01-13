package com.bellako.kiwi.features.quests.model

import com.bellako.kiwi.features.quests.data.QuestDTO
import com.bellako.kiwi.features.quests.data.QuestDataMapper
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.SubquestDomain

class QuestsRepository(
    private val api: IQuestsAPI,
) {
    suspend fun getActiveQuests(): List<QuestDomain> = api.getActiveQuests().map { QuestDataMapper.toDomain(it) }

    suspend fun getCompletedQuests(): List<QuestDomain> = api.getCompletedQuests().map { QuestDataMapper.toDomain(it) }

    suspend fun getQuest(questId: Int): QuestDomain = QuestDataMapper.toDomain(api.getQuest(questId))

    suspend fun getSubquest(subquestId: Int): SubquestDomain = QuestDataMapper.toDomain(api.getSubquest(subquestId))

    suspend fun giveQuest(questId: Int): QuestDomain = QuestDataMapper.toDomain(api.giveQuest(questId))

    suspend fun completeSubquest(subquestId: Int): QuestDTO = api.completeSubquest(subquestId)

    suspend fun failSubquest(subquestId: Int): QuestDTO = api.failSubquest(subquestId)
}
