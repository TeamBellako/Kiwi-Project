package com.bellako.kiwi.features.goals.tests

import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState

object GoalsTestFactory {
    fun validGoalState(
        id: String = "goal1",
        objective: Int = 30,
        description: String = "Complete 30 minutes of physical exercise",
        type: String = "EXERCISE",
        category: String = "DAILY_CHALLENGES",
        status: String = "REVIEW",
        points: Int = 10,
    ) = GoalState(
        id = id,
        objective = objective,
        description = description,
        type = type,
        category = category,
        status = status,
        points = points,
    )

    fun validGoalsListState(
        goals: List<GoalState> =
            listOf(
                validGoalState(id = "1", description = "Exercise for 30 minutes"),
                validGoalState(id = "2", description = "Read for 20 minutes"),
                validGoalState(id = "3", description = "Meditate for 10 minutes"),
            ),
        isLoading: Boolean = false,
        error: String? = null,
    ) = GoalsListState(
        goals = goals,
        isLoading = isLoading,
        error = error,
    )
}
