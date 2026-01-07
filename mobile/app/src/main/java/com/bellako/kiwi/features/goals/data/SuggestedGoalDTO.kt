package com.bellako.kiwi.features.goals.data

data class SuggestedGoalDTO(
    val id: String,
    val objective: Long,
    val description: String,
    val type: GoalType,
    val category: String,
    val points: Int,
)
