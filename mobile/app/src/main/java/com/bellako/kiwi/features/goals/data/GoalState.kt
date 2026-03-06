package com.bellako.kiwi.features.goals.data

data class GoalState(
    val id: Long = 0,
    val goalId: Long = 0,
    val name: String = "",
    val target: Int = 0,
    val action: String = "",
    val type: String = "EXERCISE",
    val category: String = "DAILY_CHALLENGES",
    val status: String = "IN_PROGRESS",
    val reward: Int = 0,
    val value: Int = 0,
    val date: String = "",
)
