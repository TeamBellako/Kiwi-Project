package com.bellako.kiwi.features.goals.data

data class GoalDTO(
    val id: String,
    val objective: String,
    val category: String,
    val status: String,
    val points: Int,
)
