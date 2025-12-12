package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.goals.data.GoalCategory
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
            // El cambio de fecha ya no actualiza el estado, se maneja externamente
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
        override suspend fun completeGoal(goalId: String): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.completeGoal(goalId)

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
        override suspend fun uncompleteGoal(goalId: String): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.uncompleteGoal(goalId)

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
                            goals = resultDTO.goals.map { GoalDataMapper.toState(it) },
                            isLoading = false,
                            error = null,
                        )
                } else {
                    _state.value =
                        _state.value.copy(
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

        @Suppress("detekt.ForbiddenComment")
        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun loadYesterdayDailyChallenges(): Result<Unit> {
            setIsLoading(true)
            _yesterdayGoalsState.value = _yesterdayGoalsState.value.copy(isLoading = true, error = null)

            val result = repository.getGoalsToReview()

            setIsLoading(false)

            @Suppress("TooGenericExceptionCaught")
            return handleResult(result) {
                val goalsListToReview = result.getOrNull()
                if (goalsListToReview != null && goalsListToReview.isNotEmpty()) {
                    try {
                        val allGoals = goalsListToReview.flatMap { it.goals }
                        // TODO: Implementar manejo automático de goals de tipo APP_USAGE
                        val filteredGoals = allGoals.filter { it.category != "APP_USAGE" }

                        // Convertir los goals a estado
                        val goals = filteredGoals.map { GoalDataMapper.toState(it) }

                        _yesterdayGoalsState.value =
                            _yesterdayGoalsState.value.copy(
                                goals = goals,
                                currentGoalIndex = 0,
                                isVisible = goals.isNotEmpty(),
                                isLoading = false,
                                error = null,
                            )
                    } catch (e: Exception) {
                        _yesterdayGoalsState.value =
                            _yesterdayGoalsState.value.copy(
                                goals = emptyList(),
                                isVisible = false,
                                isLoading = false,
                                error = "Error al procesar goals: ${e.message}",
                            )
                    }
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

        @Suppress("TooGenericExceptionCaught")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun markYesterdayGoalAsCompleted(
            goalId: String,
            completed: Boolean,
        ) {
            viewModelScope.launch {
                try {
                    // Llamar al backend para persistir el cambio
                    val result =
                        if (completed) {
                            repository.completeGoal(goalId)
                        } else {
                            repository.uncompleteGoal(goalId)
                        }

                    if (result.isSuccess) {
                        // Si la actualización fue exitosa, actualizar el estado local
                        val updatedGoal = GoalDataMapper.toState(result.getOrNull()!!)
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
                        // Si falla la actualización, registrar el error pero avanzar al siguiente goal
                        android.util.Log.e(
                            "GoalsViewModel",
                            "Error al actualizar goal $goalId: ${result.exceptionOrNull()?.message}",
                        )

                        val currentIndex = _yesterdayGoalsState.value.currentGoalIndex
                        val nextIndex = currentIndex + 1

                        if (nextIndex >= _yesterdayGoalsState.value.goals.size) {
                            // Ya no hay más goals, cerrar el modal
                            _yesterdayGoalsState.value =
                                _yesterdayGoalsState.value.copy(
                                    currentGoalIndex = nextIndex,
                                    isVisible = false,
                                    error = "Error al guardar algunos cambios: ${result.exceptionOrNull()?.message}",
                                )
                        } else {
                            // Pasar al siguiente goal aunque haya fallado
                            _yesterdayGoalsState.value =
                                _yesterdayGoalsState.value.copy(
                                    currentGoalIndex = nextIndex,
                                    error = "Error al guardar: ${result.exceptionOrNull()?.message}",
                                )
                        }
                    }
                } catch (e: Exception) {
                    // Capturar cualquier excepción inesperada
                    android.util.Log.e("GoalsViewModel", "Excepción inesperada al marcar goal: ${e.message}", e)

                    val currentIndex = _yesterdayGoalsState.value.currentGoalIndex
                    val nextIndex = currentIndex + 1

                    if (nextIndex >= _yesterdayGoalsState.value.goals.size) {
                        _yesterdayGoalsState.value =
                            _yesterdayGoalsState.value.copy(
                                currentGoalIndex = nextIndex,
                                isVisible = false,
                                error = "Error inesperado: ${e.message}",
                            )
                    } else {
                        _yesterdayGoalsState.value =
                            _yesterdayGoalsState.value.copy(
                                currentGoalIndex = nextIndex,
                                error = "Error inesperado: ${e.message}",
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
