package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.goals.data.GoalDataMapper
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.SuggestedGoalDataMapper
import com.bellako.kiwi.features.goals.data.SuggestedGoalDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun createGoals(
            date: String,
            goals: List<GoalState>,
        ): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val goalsDTO = goals.map { GoalDataMapper.toDTO(it) }
            val result = repository.createGoals(goalsDTO)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResult(result) {
                val resultDTOs = result.getOrNull()!!
                _state.value =
                    _state.value.copy(
                        goals = resultDTOs.map { GoalDataMapper.toState(it) },
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
        override suspend fun updateGoalProgress(goalId: String): Result<GoalDomain> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.updateGoalProgress(goalId)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { updatedDTO ->
                val updatedState = GoalDataMapper.toState(updatedDTO)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedState.id) updatedState else it }
                _state.value =
                    _state.value.copy(
                        goals = updatedGoals,
                        isLoading = false,
                        error = null,
                    )
                GoalDataMapper.toDomain(updatedDTO)
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
        override suspend fun updateGoal(goal: GoalDomain): Result<GoalDomain> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.updateGoal(GoalDataMapper.toDTO(goal))

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { updatedDTO ->
                val updatedState = GoalDataMapper.toState(updatedDTO)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedState.id) updatedState else it }
                _state.value =
                    _state.value.copy(
                        goals = updatedGoals,
                        isLoading = false,
                        error = null,
                    )
                GoalDataMapper.toDomain(updatedDTO)
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
        override suspend fun createGoalsFromSuggestions(suggestedGoals: List<SuggestedGoalDomain>): Result<Unit> {
            val today = dateToString(LocalDate.now())
            val goalsToCreate = suggestedGoals.map { SuggestedGoalDataMapper.toGoalState(it, today) }
            return createGoals(today, goalsToCreate)
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
        override suspend fun getGoalsByDate(date: String): Result<List<GoalDomain>> {
            setIsLoading(true)
            setUiState(UIState.Loading)

            val result = repository.getGoalsByDate(stringToDate(date))

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { goalDTOs ->
                goalDTOs?.map { GoalDataMapper.toDomain(it) } ?: emptyList()
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

        override suspend fun getGoalsInProgress(): Result<List<GoalDomain>> {
            setIsLoading(true)
            setUiState(UIState.Loading)

            val result = repository.getGoalsInProgress()

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { goalDTOs ->
                goalDTOs.map { GoalDataMapper.toDomain(it) }
            }
        }

        override suspend fun getSuggestedGoals(): Result<List<SuggestedGoalDomain>> {
            setIsLoading(true)
            setUiState(UIState.Loading)

            val result = repository.getSuggestedGoals()

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { suggestedGoalDTOs ->
                suggestedGoalDTOs.map { SuggestedGoalDataMapper.toDomain(it) }
            }
        }
    }
