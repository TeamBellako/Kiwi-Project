package com.bellako.kiwi.features.quests.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestsState
import kotlinx.coroutines.flow.SharedFlow

interface IQuestsViewModel : IBaseViewModel<QuestsState> {
    fun notifyNewQuest(quest: QuestDomain)

    fun notifyQuestCompleted(quest: QuestDomain)

    fun notifySubquestCompleted(
        quest: QuestDomain,
        subquestId: Int,
    )

    fun notifySubquestFailed(
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
