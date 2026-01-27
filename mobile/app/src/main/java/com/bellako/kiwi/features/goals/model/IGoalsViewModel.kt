package com.bellako.kiwi.features.goals.model

import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.SuggestedGoalDomain
import kotlinx.coroutines.flow.StateFlow

interface IGoalsViewModel {
    val state: StateFlow<GoalsListState>

    suspend fun createGoalsFromSuggestions(suggestedGoals: List<SuggestedGoalDomain>): Result<Unit>

    suspend fun updateGoalProgress(goalId: Long): Result<GoalDomain>

    suspend fun updateGoal(goal: GoalDomain): Result<GoalDomain>

    suspend fun completeGoal(goalId: Long): Result<Unit>

    suspend fun uncompleteGoal(goalId: Long): Result<Unit>

    suspend fun getGoalsByDate(date: String): Result<List<GoalDomain>>

    suspend fun getGoalsInProgress(): Result<List<GoalDomain>>

    suspend fun loadAllGoals(): Result<Unit>

    suspend fun getSuggestedGoals(): Result<List<SuggestedGoalDomain>>

    suspend fun checkAndNotifyGoals(
        onYesterdayClick: (List<GoalDomain>) -> Unit = {},
        onTodayClick: (List<GoalDomain>) -> Unit = {},
    )
}
