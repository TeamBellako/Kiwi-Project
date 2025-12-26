package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface IGoalsViewModel {
    val state: StateFlow<GoalsListState>

    fun onDateChanged(newDate: LocalDate)

    suspend fun createGoals(
        date: String,
        goals: List<GoalState>,
    ): Result<Unit>

    suspend fun completeGoal(goalId: String): Result<Unit>

    suspend fun uncompleteGoal(goalId: String): Result<Unit>

    suspend fun getGoalsByDate(date: String): Result<List<GoalDomain>>

    suspend fun getGoalsInProgress(): Result<List<GoalDomain>>

    suspend fun loadAllGoals(): Result<Unit>
}
