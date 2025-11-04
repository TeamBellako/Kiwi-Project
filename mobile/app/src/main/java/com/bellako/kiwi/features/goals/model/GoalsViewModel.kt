package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.goals.data.GoalDataMapper
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListDTO
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.YesterdayGoalsState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class GoalsViewModel
    @Inject
    constructor(
        private val repository: GoalsRepository,
    ) : BaseViewModel(),
        IGoalsViewModel {
        private val _state = MutableStateFlow(GoalsListState())
        override val state: StateFlow<GoalsListState> = _state.asStateFlow()

        private val _yesterdayGoalsState = MutableStateFlow(YesterdayGoalsState())
        override val yesterdayGoalsState: StateFlow<YesterdayGoalsState> = _yesterdayGoalsState.asStateFlow()

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDateChanged(newDate: LocalDate) {
            _state.value = _state.value.copy(date = dateToString(newDate))
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun createGoals(
            date: String,
            goals: List<GoalState>,
        ): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val goalsDTO = goals.map { GoalDataMapper.toDTO(it) }
            val dto = GoalsListDTO(date = date, goals = goalsDTO)
            val result = repository.createGoals(dto)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResult(result) {
                val resultDTO = result.getOrNull()!!
                _state.value =
                    _state.value.copy(
                        date = resultDTO.date,
                        goals = resultDTO.goals.map { GoalDataMapper.toState(it) },
                        isLoading = false,
                        error = null,
                    )
            }.also {
                if (it.isFailure) {
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = it.exceptionOrNull()?.message,
                        )
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun updateGoal(goal: GoalState): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val dto = GoalDataMapper.toDTO(goal)
            val result = repository.updateGoal(dto)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResult(result) {
                val updatedGoal = GoalDataMapper.toState(result.getOrNull()!!)
                val updatedGoals =
                    _state.value.goals.map {
                        if (it.id == updatedGoal.id) updatedGoal else it
                    }
                _state.value =
                    _state.value.copy(
                        goals = updatedGoals,
                        isLoading = false,
                        error = null,
                    )
            }.also {
                if (it.isFailure) {
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = it.exceptionOrNull()?.message,
                        )
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun loadGoalsByDate(date: String): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.getGoalsByDate(stringToDate(date))

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResult(result) {
                val resultDTO = result.getOrNull()
                if (resultDTO != null) {
                    _state.value =
                        _state.value.copy(
                            date = resultDTO.date,
                            goals = resultDTO.goals.map { GoalDataMapper.toState(it) },
                            isLoading = false,
                            error = null,
                        )
                } else {
                    _state.value =
                        _state.value.copy(
                            date = date,
                            goals = emptyList(),
                            isLoading = false,
                            error = null,
                        )
                }
            }.also {
                if (it.isFailure) {
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = it.exceptionOrNull()?.message,
                        )
                }
            }
        }

        override suspend fun loadAllGoals(): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.getAllGoals()

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResult(result) {
                // Este método podría devolver una lista de todos los días con goals
                // Por ahora solo actualizamos el estado de loading
                _state.value =
                    _state.value.copy(
                        isLoading = false,
                        error = null,
                    )
            }.also {
                if (it.isFailure) {
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = it.exceptionOrNull()?.message,
                        )
                }
            }
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

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun loadYesterdayDailyChallenges(): Result<Unit> {
            setIsLoading(true)
            _yesterdayGoalsState.value = _yesterdayGoalsState.value.copy(isLoading = true, error = null)

            val yesterday = LocalDate.now().minusDays(1)
            val result = repository.getGoalsByDate(yesterday)

            setIsLoading(false)

            return handleResult(result) {
                val resultDTO = result.getOrNull()
                if (resultDTO != null) {
                    // Filtrar solo los goals que estén en estado REVIEW
                    val goalsToReview =
                        resultDTO.goals
                            .filter { it.status == "REVIEW" }
                            .map { GoalDataMapper.toState(it) }

                    _yesterdayGoalsState.value =
                        _yesterdayGoalsState.value.copy(
                            goals = goalsToReview,
                            currentGoalIndex = 0,
                            isVisible = goalsToReview.isNotEmpty(),
                            isLoading = false,
                            error = null,
                        )
                } else {
                    _yesterdayGoalsState.value =
                        _yesterdayGoalsState.value.copy(
                            goals = emptyList(),
                            isVisible = false,
                            isLoading = false,
                            error = null,
                        )
                }
            }.also {
                if (it.isFailure) {
                    _yesterdayGoalsState.value =
                        _yesterdayGoalsState.value.copy(
                            isLoading = false,
                            error = it.exceptionOrNull()?.message,
                        )
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun markYesterdayGoalAsCompleted(
            goalId: String,
            completed: Boolean,
        ) {
            viewModelScope.launch {
                // Encontrar el goal actual
                val currentGoal = _yesterdayGoalsState.value.goals.find { it.id == goalId }

                if (currentGoal != null) {
                    // Actualizar el estado del goal
                    val updatedGoal =
                        currentGoal.copy(
                            status = if (completed) "COMPLETED" else "NOT_COMPLETED",
                        )

                    // Llamar al backend para persistir el cambio
                    val result = repository.updateGoal(GoalDataMapper.toDTO(updatedGoal))

                    if (result.isSuccess) {
                        // Si la actualización fue exitosa, actualizar el estado local
                        val updatedGoals =
                            _yesterdayGoalsState.value.goals.map { goal ->
                                if (goal.id == goalId) {
                                    updatedGoal
                                } else {
                                    goal
                                }
                            }

                        val currentIndex = _yesterdayGoalsState.value.currentGoalIndex
                        val nextIndex = currentIndex + 1

                        if (nextIndex >= updatedGoals.size) {
                            // Ya no hay más goals, cerrar el modal
                            _yesterdayGoalsState.value =
                                _yesterdayGoalsState.value.copy(
                                    goals = updatedGoals,
                                    currentGoalIndex = nextIndex,
                                    isVisible = false,
                                )
                        } else {
                            // Pasar al siguiente goal
                            _yesterdayGoalsState.value =
                                _yesterdayGoalsState.value.copy(
                                    goals = updatedGoals,
                                    currentGoalIndex = nextIndex,
                                )
                        }
                    } else {
                        // Manejar el error (opcional: mostrar mensaje)
                        _yesterdayGoalsState.value =
                            _yesterdayGoalsState.value.copy(
                                error = result.exceptionOrNull()?.message,
                            )
                    }
                }
            }
        }

        override fun closeYesterdayGoalsModal() {
            _yesterdayGoalsState.value =
                _yesterdayGoalsState.value.copy(
                    isVisible = false,
                    currentGoalIndex = 0,
                )
        }
    }
