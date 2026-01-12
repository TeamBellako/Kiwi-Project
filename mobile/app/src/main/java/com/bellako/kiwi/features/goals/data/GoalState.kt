package com.bellako.kiwi.features.goals.data

data class GoalState(
    val id: String = "",
    val target: Int = 0,
    val action: String = "",
    val type: String = "EXERCISE",
    val category: String = "DAILY_CHALLENGES",
    val status: String = "REVIEW",
    val reward: Int = 0,
    val value: Int = 0,
    val date: String = "",
)
