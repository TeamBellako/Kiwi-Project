package com.bellako.kiwi.features.quests.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestsState
import kotlinx.coroutines.flow.SharedFlow

sealed class QuestNotificationEvent {
    data class NewQuest(
        val quest: QuestDomain,
    ) : QuestNotificationEvent()

    data class QuestCompleted(
        val quest: QuestDomain,
    ) : QuestNotificationEvent()

    data class SubquestCompleted(
        val quest: QuestDomain,
        val subquestId: Int,
    ) : QuestNotificationEvent()

    data class SubquestFailed(
        val quest: QuestDomain,
        val subquestId: Int,
    ) : QuestNotificationEvent()
}

interface IQuestsViewModel : IBaseViewModel<QuestsState> {
    fun getNotifications(): SharedFlow<QuestNotificationEvent>

    suspend fun notifyNewQuest(quest: QuestDomain)

    suspend fun notifyQuestCompleted(quest: QuestDomain)

    suspend fun notifySubquestCompleted(
        quest: QuestDomain,
        subquestId: Int,
    )

    suspend fun notifySubquestFailed(
        quest: QuestDomain,
        subquestId: Int,
    )

    fun loadActiveQuests()

    suspend fun isQuestCompleted(questId: Int): Boolean

    suspend fun isSubquestCompleted(subquestId: Int): Boolean

    suspend fun isSubquestFailed(subquestId: Int): Boolean

    fun giveQuest(questId: Int)

    fun completeSubquest(subquestId: Int)

    fun failSubquest(subquestId: Int)
}
