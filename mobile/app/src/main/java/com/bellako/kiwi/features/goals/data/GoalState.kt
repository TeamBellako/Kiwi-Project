package com.bellako.kiwi.features.goals.data

data class GoalState(
    val id: String = "",
    val objective: Long = 0,
    val description: String = "",
    val type: String = "EXERCISE",
    val category: String = "DAILY_CHALLENGES",
    val status: String = "REVIEW",
    val points: Int = 0,
)
