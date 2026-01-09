package com.bellako.kiwi.features.goals.data

data class SuggestedGoalDTO(
    val id: String,
    val objective: Int,
    val description: String,
    val type: GoalType,
    val category: String,
    val points: Int,
)
