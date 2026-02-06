package com.bellako.kiwi.features.goals.data

data class SuggestedGoalDTO(
    val id: Long,
    val target: Long,
    val action: String,
    val type: String,
    val category: String,
    val reward: Int,
)
