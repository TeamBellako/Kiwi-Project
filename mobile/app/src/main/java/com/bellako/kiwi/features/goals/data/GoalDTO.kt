package com.bellako.kiwi.features.goals.data

data class GoalDTO(
    val id: String,
    val objective: Int,
    val description: String,
    val type: GoalType,
    val category: String,
    val status: String,
    val points: Int,
    val date: String = "",
)
