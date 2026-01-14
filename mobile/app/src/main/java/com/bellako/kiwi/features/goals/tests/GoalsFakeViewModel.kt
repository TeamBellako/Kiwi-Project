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

@Suppress("EmptyFunctionBlock")
class GoalsFakeViewModel(
    initialState: GoalsListState = GoalsTestFactory.validGoalsListState(),
) : BaseFakeViewModel(),
    IGoalsViewModel {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<GoalsListState> = _state.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    // Crear una lista inicial de goals y un diccionario mutable para consultas/actualizaciones
    @Suppress("MagicNumber")
    private val initialGoals =
        listOf(
            GoalDomain(
                "1",
                10,
                "Meditate for at least 10 minutes",
                GoalType.MEDITATION,
                GoalCategory.DAILY_CHALLENGES,
                GoalStatus.IN_PROGRESS,
                150,
                value = 0,
            ),
            GoalDomain(
                "2",
                30,
                "Exercise for 30 minutes",
                GoalType.EXERCISE,
                GoalCategory.DAILY_CHALLENGES,
                GoalStatus.IN_PROGRESS,
                200,
                value = 10,
            ),
        )

    private val fakeGoalsMap: MutableMap<String, GoalDomain> =
        initialGoals.associateBy { it.id }.toMutableMap()

    // ---------------------------------------------------------------------------------------------

    override suspend fun createGoalsFromSuggestions(suggestedGoals: List<SuggestedGoalDomain>): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun updateGoalProgress(goalId: String): Result<GoalDomain> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            val existing =
                fakeGoalsMap[goalId]
                    ?: return Result.failure(Exception("Goal with id $goalId not found"))

            // Simular incremento de progreso: +0.5f y clamped a 1f
            val newValue = (existing.value + 1).coerceAtMost(existing.target)
            val newStatus = if (newValue >= existing.target) GoalStatus.COMPLETED else existing.status
            val updated = existing.copy(value = newValue, status = newStatus)
            fakeGoalsMap[goalId] = updated
            Result.success(updated)
        }

    override suspend fun updateGoal(goal: GoalDomain): Result<GoalDomain> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            fakeGoalsMap[goal.id] = goal
            Result.success(goal)
        }

    override suspend fun completeGoal(goalId: String): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            val existing =
                fakeGoalsMap[goalId]
                    ?: return Result.failure(Exception("Goal with id $goalId not found"))
            val updated = existing.copy(value = 1, status = GoalStatus.COMPLETED)
            fakeGoalsMap[goalId] = updated
            Result.success(Unit)
        }

    override suspend fun uncompleteGoal(goalId: String): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            val existing =
                fakeGoalsMap[goalId]
                    ?: return Result.failure(Exception("Goal with id $goalId not found"))
            val updated = existing.copy(value = 0, status = GoalStatus.IN_PROGRESS)
            fakeGoalsMap[goalId] = updated
            Result.success(Unit)
        }

    override suspend fun getGoalsByDate(date: String): Result<List<GoalDomain>> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            // Para el fake siempre devolvemos las goals almacenadas en el diccionario
            Result.success(fakeGoalsMap.values.toList())
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
            // Devolver solo las goals que no estén completadas
            Result.success(fakeGoalsMap.values.filter { it.status != GoalStatus.COMPLETED })
        }

    override suspend fun getSuggestedGoals(): Result<List<SuggestedGoalDomain>> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(emptyList())
        }

    override suspend fun checkAndNotifyGoals(
        onYesterdayClick: (List<GoalDomain>) -> Unit,
        onTodayClick: (List<GoalDomain>) -> Unit,
    ) {
        // Implementación fake - no hace nada
    }
}
