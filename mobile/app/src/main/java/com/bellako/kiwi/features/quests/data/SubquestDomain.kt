package com.bellako.kiwi.features.quests.data

data class SubquestDomain(
    val id: Int,
    val name: String,
    val experience: Int,
    val order: Int,
    val status: SubquestStatus,
    val onCompletedEvent: String,
    val onCompletedEntityId: Int,
)

enum class SubquestStatus { LOCKED, ACTIVE, COMPLETED, FAILED }
