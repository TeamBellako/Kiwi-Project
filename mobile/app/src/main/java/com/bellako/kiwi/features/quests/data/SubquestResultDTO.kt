package com.bellako.kiwi.features.quests.data

data class SubquestResultDTO(
    val updatedSubquest: SubquestDTO?,
    val nextSubquest: SubquestDTO?,
    val completedQuest: QuestDTO?,
)
