package com.bellako.kiwi.features.quests.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.quests.data.QuestsState

interface IQuestsViewModel : IBaseViewModel<QuestsState> {
    fun loadActiveQuests()

    fun loadCompletedQuests()

    fun giveQuest(questId: Int)

    fun completeSubquest(subquestId: Int)

    fun failSubquest(subquestId: Int)
}
