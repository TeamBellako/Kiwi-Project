package com.bellako.kiwi.features.quests.data

data class SubquestDTO(
    val subquestId: Int,
    val name: String,
    val experience: Int,
    val order: Int,
    val status: String,
    val onCompletedEvent: String,
    val onCompletedEntityId: Int,
)
