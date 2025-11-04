package com.bellako.kiwi.features.goals.tests

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.YesterdayGoalsState
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

@Suppress("EmptyFunctionBlock")
class GoalsFakeViewModel(
    initialState: GoalsListState = GoalsTestFactory.validGoalsListState(),
    initialYesterdayState: YesterdayGoalsState = GoalsTestFactory.validYesterdayGoalsState(),
) : BaseFakeViewModel(),
    IGoalsViewModel {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<GoalsListState> = _state.asStateFlow()

    private val _yesterdayGoalsState = MutableStateFlow(initialYesterdayState)
    override val yesterdayGoalsState: StateFlow<YesterdayGoalsState> = _yesterdayGoalsState.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    // ---------------------------------------------------------------------------------------------

    override fun onDateChanged(newDate: LocalDate) {}

    override suspend fun createGoals(
        date: String,
        goals: List<GoalState>,
    ): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun updateGoal(goal: GoalState): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun loadGoalsByDate(date: String): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun loadAllGoals(): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override fun completeGoal(goalId: String) {
        val updatedGoals =
            _state.value.goals.map { goal ->
                if (goal.id == goalId) {
                    goal.copy(status = "COMPLETED")
                } else {
                    goal
                }
            }
        _state.value = _state.value.copy(goals = updatedGoals)
    }

    override suspend fun loadYesterdayDailyChallenges(): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markYesterdayGoalAsCompleted(
        goalId: String,
        completed: Boolean,
    ) {
        val currentState = _yesterdayGoalsState.value
        val updatedGoals =
            currentState.goals.map { goal ->
                if (goal.id == goalId) {
                    goal.copy(status = if (completed) "COMPLETED" else "NOT_COMPLETED")
                } else {
                    goal
                }
            }

        // Avanzar al siguiente goal
        val nextIndex = currentState.currentGoalIndex + 1
        if (nextIndex < updatedGoals.size) {
            _yesterdayGoalsState.value =
                currentState.copy(
                    goals = updatedGoals,
                    currentGoalIndex = nextIndex,
                )
        } else {
            // Si no hay más goals, cerrar el modal
            _yesterdayGoalsState.value =
                currentState.copy(
                    goals = updatedGoals,
                    currentGoalIndex = nextIndex,
                    isVisible = false,
                )
        }
    }

    override fun closeYesterdayGoalsModal() {
        _yesterdayGoalsState.value = _yesterdayGoalsState.value.copy(isVisible = false)
    }
}
