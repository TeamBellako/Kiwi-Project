package com.bellako.kiwi.features.quests.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestsState
import kotlinx.coroutines.flow.SharedFlow

interface IQuestsViewModel : IBaseViewModel<QuestsState> {
    fun getNotifications(): SharedFlow<QuestNotificationEvent>

    suspend fun notifyNewQuest(quest: QuestDomain)

    suspend fun notifyQuestCompleted(quest: QuestDomain)

    suspend fun notifySubquestCompleted(quest: QuestDomain, subquestId: Int)
    suspend fun notifySubquestFailed(quest: QuestDomain, subquestId: Int)

    fun loadActiveQuests()

    fun loadCompletedQuests()

    fun giveQuest(questId: Int)

    fun completeSubquest(subquestId: Int)

    fun failSubquest(subquestId: Int)
}
