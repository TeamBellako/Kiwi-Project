package com.bellako.kiwi.features.goals.model

import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.SuggestedGoalDomain
import kotlinx.coroutines.flow.StateFlow

interface IGoalsViewModel {
    val state: StateFlow<GoalsListState>

    suspend fun createGoalsFromSuggestions(suggestedGoals: List<SuggestedGoalDomain>): Result<Unit>

    suspend fun updateGoalProgress(goalId: String): Result<GoalDomain>

    suspend fun updateGoal(goal: GoalDomain): Result<GoalDomain>

    suspend fun completeGoal(goalId: String): Result<Unit>

    suspend fun uncompleteGoal(goalId: String): Result<Unit>

    suspend fun getGoalsByDate(date: String): Result<List<GoalDomain>>

    suspend fun getGoalsInProgress(): Result<List<GoalDomain>>

    suspend fun loadAllGoals(): Result<Unit>

    suspend fun getSuggestedGoals(): Result<List<SuggestedGoalDomain>>
}
