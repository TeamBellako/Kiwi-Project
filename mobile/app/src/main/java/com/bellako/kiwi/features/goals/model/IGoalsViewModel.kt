package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.YesterdayGoalsState
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface IGoalsViewModel {
    val state: StateFlow<GoalsListState>
    val yesterdayGoalsState: StateFlow<YesterdayGoalsState>

    fun onDateChanged(newDate: LocalDate)

    suspend fun createGoals(
        date: String,
        goals: List<GoalState>,
    ): Result<Unit>

    suspend fun updateGoal(goal: GoalState): Result<Unit>

    suspend fun loadGoalsByDate(date: String): Result<Unit>

    suspend fun loadAllGoals(): Result<Unit>

    fun completeGoal(goalId: String)

    suspend fun loadYesterdayDailyChallenges(): Result<Unit>

    @RequiresApi(Build.VERSION_CODES.O)
    fun markYesterdayGoalAsCompleted(
        goalId: String,
        completed: Boolean,
    )

    fun closeYesterdayGoalsModal()
}
