package com.bellako.kiwi.features.goals.data

data class GoalDomain(
    val id: String,
    val objective: String,
    val description: String,
    val type: GoalType,
    val category: GoalCategory,
    val status: GoalStatus,
    val points: Int,
    val progress: Float = 0f,
    val date: String = "",
)
