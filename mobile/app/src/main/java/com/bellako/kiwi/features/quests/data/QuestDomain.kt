package com.bellako.kiwi.features.quests.data

data class QuestDomain(
    val id: Int,
    val name: String,
    val description: String,
    val experience: Int,
    val icon: Int,
    val status: QuestStatus,
    val subquests: List<SubquestDomain>,
    val onCompletedEvent: String,
    val onCompletedEntityId: Int,
)

enum class QuestStatus { ACTIVE, COMPLETED }
