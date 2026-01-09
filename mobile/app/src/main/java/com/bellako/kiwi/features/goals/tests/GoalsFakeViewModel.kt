package com.bellako.kiwi.features.goals.tests

import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.SuggestedGoalDomain
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

@Suppress("EmptyFunctionBlock")
class GoalsFakeViewModel(
    initialState: GoalsListState = GoalsTestFactory.validGoalsListState(),
) : BaseFakeViewModel(),
    IGoalsViewModel {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<GoalsListState> = _state.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    // ---------------------------------------------------------------------------------------------

    override fun onDateChanged(newDate: LocalDate) {}

    override suspend fun createGoalsFromSuggestions(suggestedGoals: List<SuggestedGoalDomain>): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun completeGoal(goalId: String): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun uncompleteGoal(goalId: String): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun getGoalsByDate(date: String): Result<List<GoalDomain>> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(
                listOf(
                    GoalDomain(
                        "1",
                        "Complete daily meditation",
                        "Meditate for at least 10 minutes",
                        GoalType.MEDITATION,
                        GoalCategory.DAILY_CHALLENGES,
                        GoalStatus.IN_PROGRESS,
                        150,
                        progress = 0.0f,
                    ),
                    GoalDomain(
                        "2",
                        "Exercise routine",
                        "Complete your workout session",
                        GoalType.EXERCISE,
                        GoalCategory.DAILY_CHALLENGES,
                        GoalStatus.IN_PROGRESS,
                        200,
                        progress = 0.5f,
                    ),
                ),
            )
        }

    override suspend fun loadAllGoals(): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun getGoalsInProgress(): Result<List<GoalDomain>> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(emptyList())
        }

    override suspend fun getSuggestedGoals(): Result<List<SuggestedGoalDomain>> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(emptyList())
        }
}
