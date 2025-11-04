package com.bellako.kiwi.features.goals.data

data class GoalState(
    val id: String = "",
    val objective: String = "",
    val category: String = "DAILY_CHALLENGES",
    val status: String = "REVIEW",
    val points: Int = 0,
)
