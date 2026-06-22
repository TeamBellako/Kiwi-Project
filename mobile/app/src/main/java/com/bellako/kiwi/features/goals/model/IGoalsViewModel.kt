package com.bellako.kiwi.features.goals.model

import com.bellako.kiwi.features.goals.data.AppUsageResult
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.UserAppUsageDTO
import com.bellako.kiwi.features.goals.data.UserGoalStatusDomain
import kotlinx.coroutines.flow.StateFlow

interface IGoalsViewModel {
    val state: StateFlow<GoalsListState>

    suspend fun createGoalsFromDefinitions(goalDefinitions: List<GoalDomain>): Result<Unit>

    suspend fun updateGoalProgress(goalId: Long): Result<UserGoalStatusDomain>

    suspend fun updateGoal(goal: UserGoalStatusDomain): Result<UserGoalStatusDomain>

    suspend fun completeGoal(goalId: Long): Result<Unit>

    suspend fun uncompleteGoal(goalId: Long): Result<Unit>

    suspend fun getGoalsByDate(date: String): Result<List<UserGoalStatusDomain>>

    suspend fun getGoalsInProgress(): Result<List<UserGoalStatusDomain>>

    suspend fun loadAllGoals(): Result<Unit>

    suspend fun getGoalDefinitions(): Result<List<GoalDomain>>

    suspend fun checkAndNotifyGoals()

    suspend fun getDailyGoalsProgress(date: String): Float

    suspend fun invalidateGoalsInProgressCache()

    suspend fun getAppsAverageUsage(
        goodApps: List<String>,
        badApps: List<String>,
    ): Result<AppUsageResult>

    suspend fun saveBaselineAppUsage(
        goodApps: List<String>,
        badApps: List<String>,
    ): Result<UserAppUsageDTO>

    suspend fun autoReviewAppUsageGoals(): Result<Unit>
}
