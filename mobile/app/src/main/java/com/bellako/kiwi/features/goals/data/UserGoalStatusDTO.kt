package com.bellako.kiwi.features.goals.data

data class UserGoalStatusDTO(
    val id: Long,
    val goalId: Long,
    val name: String,
    val target: Int,
    val action: String,
    val type: String,
    val category: String,
    val status: String,
    val reward: Int,
    val date: String = "",
    val value: Int = 0,
    val onCompletedEvent: String = "_",
    val onCompletedEntityId: Int = 0,
)
