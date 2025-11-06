package com.bellako.kiwi.features.goals.tests

import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.YesterdayGoalsState

object GoalsTestFactory {
    fun validGoalState(
        id: String = "goal1",
        objective: String = "Exercise for 30 minutes",
        category: String = "DAILY_CHALLENGES",
        status: String = "REVIEW",
        points: Int = 10,
    ) = GoalState(
        id = id,
        objective = objective,
        category = category,
        status = status,
        points = points,
    )

    fun validGoalsListState(
        goals: List<GoalState> =
            listOf(
                validGoalState(id = "1", objective = "Exercise for 30 minutes"),
                validGoalState(id = "2", objective = "Read for 20 minutes"),
                validGoalState(id = "3", objective = "Meditate for 10 minutes"),
            ),
        isLoading: Boolean = false,
        error: String? = null,
    ) = GoalsListState(
        goals = goals,
        isLoading = isLoading,
        error = error,
    )

    fun validYesterdayGoalsState(
        goals: List<GoalState> =
            listOf(
                validGoalState(id = "1", objective = "Exercise for 30 minutes"),
                validGoalState(id = "2", objective = "Read for 20 minutes"),
                validGoalState(id = "3", objective = "Meditate for 10 minutes"),
            ),
        currentGoalIndex: Int = 0,
        isVisible: Boolean = true,
        isLoading: Boolean = false,
        error: String? = null,
    ) = YesterdayGoalsState(
        goals = goals,
        currentGoalIndex = currentGoalIndex,
        isVisible = isVisible,
        isLoading = isLoading,
        error = error,
    )
}
