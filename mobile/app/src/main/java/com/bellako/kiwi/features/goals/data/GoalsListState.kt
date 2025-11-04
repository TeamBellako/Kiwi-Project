package com.bellako.kiwi.features.goals.data

data class GoalsListState(
    val date: String = "",
    val goals: List<GoalState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
