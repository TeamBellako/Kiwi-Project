package com.bellako.kiwi.features.goals.data

data class YesterdayGoalsState(
    val goals: List<GoalState> = emptyList(),
    val currentGoalIndex: Int = 0,
    val isVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val currentGoal: GoalState?
        get() = if (currentGoalIndex < goals.size) goals[currentGoalIndex] else null

    val hasMoreGoals: Boolean
        get() = currentGoalIndex < goals.size - 1

    val isFinished: Boolean
        get() = currentGoalIndex >= goals.size
}
