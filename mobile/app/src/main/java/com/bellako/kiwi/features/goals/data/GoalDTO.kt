package com.bellako.kiwi.features.goals.data

data class GoalDTO(
    val id: Long,
    val name: String,
    val target: Int,
    val action: String,
    val type: String,
    val category: String,
    val reward: Int,
    val onCompletedEvent: String = "_",
    val onCompletedEntityId: Int = 0,
)
