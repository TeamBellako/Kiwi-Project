package com.bellako.kiwi.features.quests.data

data class QuestDTO(
    val questId: Int,
    val name: String,
    val description: String,
    val experience: Int,
    val status: String,
    val subquests: List<SubquestDTO>,
)
