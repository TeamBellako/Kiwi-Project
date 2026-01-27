package com.bellako.kiwi.features.goals.data

data class GoalDTO(
    val id: Long,
    val target: Int,
    val action: String,
    val type: GoalType,
    val category: String,
    val status: String,
    val reward: Int,
    val date: String = "",
    val value: Int = 0,
)
