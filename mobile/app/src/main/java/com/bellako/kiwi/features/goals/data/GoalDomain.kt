package com.bellako.kiwi.features.goals.data

data class GoalDomain(
    val id: String,
    val objective: String,
    val category: GoalCategory,
    val status: GoalStatus,
    val points: Int,
)
