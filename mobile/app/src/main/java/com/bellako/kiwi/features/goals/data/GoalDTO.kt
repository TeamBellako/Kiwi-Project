package com.bellako.kiwi.features.goals.data

data class GoalDTO(
    val id: String,
    val objective: Long,
    val description: String,
    val type: GoalType,
    val category: String,
    val status: String,
    val points: Int,
)
