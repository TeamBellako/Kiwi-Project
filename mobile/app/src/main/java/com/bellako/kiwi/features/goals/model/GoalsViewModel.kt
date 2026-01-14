package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.goals.data.GoalDataMapper
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalModalType
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.SuggestedGoalDataMapper
import com.bellako.kiwi.features.goals.data.SuggestedGoalDomain
import com.bellako.kiwi.features.goals.screens.GoalsNotificationCard
import com.bellako.kiwi.features.notifications.model.NotificationEvent
import com.bellako.kiwi.features.notifications.model.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

@HiltViewModel
class GoalsViewModel
    @Inject
    constructor(
        private val repository: GoalsRepository,
        private val notificationManager: NotificationManager,
    ) : BaseViewModel(),
        IGoalsViewModel {
        private val _state = MutableStateFlow(GoalsListState())
        override val state: StateFlow<GoalsListState> = _state.asStateFlow()

        // Cache de goals por fecha
        private val cachedGoalsByDate = mutableMapOf<String, List<GoalDomain>>()
        private var cachedGoalsInProgress: List<GoalDomain>? = null
        private var cachedSuggestedGoals: List<SuggestedGoalDomain>? = null

        private var cacheDate: String? = null

        // Mutex para proteger el acceso concurrente a la cache
        private val cacheMutex = Mutex()

        private fun getCurrentDate(): String = dateToString(LocalDate.now())

        /**
         * Actualiza un goal en todas las entradas del cache donde aparezca
         * @param updatedGoal El goal actualizado
         */
        private suspend fun updateGoalInCache(updatedGoal: GoalDomain) {
            cacheMutex.withLock {
                // Actualizar en cachedGoalsByDate
                val keys = cachedGoalsByDate.keys.toList()
                keys.forEach { date ->
                    val goals = cachedGoalsByDate[date] ?: emptyList()
                    val updatedList = goals.map { goal -> if (goal.id == updatedGoal.id) updatedGoal else goal }
                    cachedGoalsByDate[date] = updatedList
                }

                // Actualizar en cachedGoalsInProgress si existe
                cachedGoalsInProgress = cachedGoalsInProgress?.map { goal -> if (goal.id == updatedGoal.id) updatedGoal else goal }

                // Mantener fecha de cache actualizada
                cacheDate = getCurrentDate()
            }
        }

        /**
         * Añade nuevas goals al cache de la fecha correspondiente
         * @param newGoals Lista de goals creadas
         * @param date Fecha en formato yyyy-MM-dd
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun addGoalsToCache(
            newGoals: List<GoalDomain>,
            date: String,
        ) {
            cacheMutex.withLock {
                val existing = cachedGoalsByDate[date]
                if (existing == null) {
                    cachedGoalsByDate[date] = newGoals
                } else {
                    cachedGoalsByDate[date] = existing + newGoals
                }
                cacheDate = getCurrentDate()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun createGoals(goals: List<GoalState>): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val goalsDTO = goals.map { GoalDataMapper.toDTO(it) }
            val result = repository.createGoals(goalsDTO)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                val resultDTOs = result.getOrNull()!!
                _state.value =
                    _state.value.copy(
                        goals = resultDTOs.map { GoalDataMapper.toState(it) },
                        isLoading = false,
                        error = null,
                    )
                val newGoalsDomain = resultDTOs.map { GoalDataMapper.toDomain(it) }
                val today = dateToString(LocalDate.now())
                addGoalsToCache(newGoalsDomain, today)
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

            // Manejar explícitamente para poder usar suspend dentro del flujo
            return if (result.isSuccess) {
                val updatedDTO = result.getOrNull()!!
                val updatedState = GoalDataMapper.toState(updatedDTO)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedState.id) updatedState else it }
                _state.value = _state.value.copy(goals = updatedGoals, isLoading = false, error = null)
                val updatedDomain = GoalDataMapper.toDomain(updatedDTO)
                updateGoalInCache(updatedDomain)
                Result.success(updatedDomain)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
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

            return if (result.isSuccess) {
                val updatedDTO = result.getOrNull()!!
                val updatedState = GoalDataMapper.toState(updatedDTO)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedState.id) updatedState else it }
                _state.value = _state.value.copy(goals = updatedGoals, isLoading = false, error = null)
                val updatedDomain = GoalDataMapper.toDomain(updatedDTO)
                updateGoalInCache(updatedDomain)
                Result.success(updatedDomain)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun createGoalsFromSuggestions(suggestedGoals: List<SuggestedGoalDomain>): Result<Unit> {
            if (suggestedGoals.size == 0) {
                return Result.failure(IllegalArgumentException("La lista de sugerencias está vacía"))
            }
            val today = dateToString(LocalDate.now())
            val goalsToCreate = suggestedGoals.map { SuggestedGoalDataMapper.toGoalState(it, today) }
            cacheMutex.withLock {
                cachedSuggestedGoals = null
                cachedGoalsByDate.remove(today)
            }
            return createGoals(goalsToCreate)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun completeGoal(goalId: String): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.completeGoal(goalId)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                val updatedGoal = GoalDataMapper.toState(result.getOrNull()!!)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedGoal.id) updatedGoal else it }
                _state.value = _state.value.copy(goals = updatedGoals, isLoading = false, error = null)
                val updatedDomain = GoalDataMapper.toDomain(result.getOrNull()!!)
                updateGoalInCache(updatedDomain)
            }.also {
                if (it.isFailure) {
                    _state.value = _state.value.copy(isLoading = false, error = it.exceptionOrNull()?.message)
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

            return handleResultSuspend(result) {
                val updatedGoal = GoalDataMapper.toState(result.getOrNull()!!)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedGoal.id) updatedGoal else it }
                _state.value = _state.value.copy(goals = updatedGoals, isLoading = false, error = null)
                val updatedDomain = GoalDataMapper.toDomain(result.getOrNull()!!)
                updateGoalInCache(updatedDomain)
            }.also {
                if (it.isFailure) {
                    _state.value = _state.value.copy(isLoading = false, error = it.exceptionOrNull()?.message)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun getGoalsByDate(date: String): Result<List<GoalDomain>> {
            // Comprobar cache atómicamente
            cacheMutex.withLock {
                if (cachedGoalsByDate.containsKey(date) && cacheDate == getCurrentDate()) {
                    return Result.success(cachedGoalsByDate[date]!!)
                }
            }

            setIsLoading(true)
            setUiState(UIState.Loading)

            val result = repository.getGoalsByDate(stringToDate(date))

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { goalDTOs ->
                val domainGoals = goalDTOs?.map { GoalDataMapper.toDomain(it) } ?: emptyList()
                cacheMutex.withLock {
                    cachedGoalsByDate[date] = domainGoals
                    cacheDate = getCurrentDate()
                }
                domainGoals
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
                _state.value = _state.value.copy(isLoading = false, error = null)
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
            cacheMutex.withLock {
                if (cachedGoalsInProgress != null && cacheDate == getCurrentDate()) {
                    return Result.success(cachedGoalsInProgress!!)
                }
            }

            setIsLoading(true)
            setUiState(UIState.Loading)

            val result = repository.getGoalsInProgress()

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { goalDTOs ->
                val domainGoals = goalDTOs.map { GoalDataMapper.toDomain(it) }
                // Guardar en cache
                cacheMutex.withLock {
                    cachedGoalsInProgress = domainGoals
                    cacheDate = getCurrentDate()
                }
                domainGoals
            }
        }

        override suspend fun getSuggestedGoals(): Result<List<SuggestedGoalDomain>> {
            cacheMutex.withLock {
                if (cachedSuggestedGoals != null && cacheDate == getCurrentDate()) {
                    return Result.success(cachedSuggestedGoals!!)
                }
            }

            setIsLoading(true)
            setUiState(UIState.Loading)

            val result = repository.getSuggestedGoals()

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { suggestedGoalDTOs ->
                val suggestedDomains = suggestedGoalDTOs.map { SuggestedGoalDataMapper.toDomain(it) }
                cacheMutex.withLock {
                    cachedSuggestedGoals = suggestedDomains
                }
                suggestedDomains
            }
        }

        // Limpiar cache cuando se destruye el ViewModel
        override fun onCleared() {
            super.onCleared()
            cachedGoalsByDate.clear()
            cachedGoalsInProgress = null
            cachedSuggestedGoals = null
            cacheDate = null
        }

        suspend fun notifyNewGoals(
            goals: List<com.bellako.kiwi.features.goals.data.IGoal>,
            onClick: () -> Unit = {},
        ) {
            notificationManager.notify(
                NotificationEvent.Goal {
                    GoalsNotificationCard(
                        type = GoalModalType.NEW,
                        goals = goals,
                        onClick = onClick,
                    )
                },
            )
        }

        suspend fun notifyYesterdayGoals(
            goals: List<com.bellako.kiwi.features.goals.data.IGoal>,
            onClick: () -> Unit = {},
        ) {
            notificationManager.notify(
                NotificationEvent.Goal {
                    GoalsNotificationCard(
                        type = GoalModalType.YESTERDAY,
                        goals = goals,
                        onClick = onClick,
                    )
                },
            )
        }

        /**
         * Evalúa si hay notificaciones de goals que mostrar y las envía automáticamente.
         * 1. Verifica si hay goals de ayer en progreso (muestra primero)
         * 2. Verifica si hay goals de hoy o sugerencias (muestra después)
         *
         * @param onYesterdayClick Callback cuando se hace click en notificación de ayer
         * @param onTodayClick Callback cuando se hace click en notificación de hoy
         */
        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun checkAndNotifyGoals(
            onYesterdayClick: (List<GoalDomain>) -> Unit,
            onTodayClick: (List<GoalDomain>) -> Unit,
        ) {
            val today = dateToString(LocalDate.now())

            val inProgressResult = getGoalsInProgress()
            val yesterdayGoals = inProgressResult.getOrNull()

            if (!yesterdayGoals.isNullOrEmpty()) {
                notifyYesterdayGoals(yesterdayGoals) {
                    onYesterdayClick(yesterdayGoals)
                }
                return // Salir para mostrar solo la de ayer primero
            }

            val todayResult = getGoalsByDate(today)
            val todayGoals = todayResult.getOrNull()

            if (!todayGoals.isNullOrEmpty()) {
                return
            }

            // 3. No hay goals de hoy - obtener sugerencias
            val suggestedResult = getSuggestedGoals()
            val suggestedGoals = suggestedResult.getOrNull()

            if (!suggestedGoals.isNullOrEmpty()) {
                notifyNewGoals(suggestedGoals) {
                    val goalsForCallback =
                        suggestedGoals.map { suggested ->
                            GoalDomain(
                                id = "", // Temporal hasta que se cree
                                target = suggested.target,
                                action = suggested.action,
                                type = suggested.type,
                                category = suggested.category,
                                status = com.bellako.kiwi.features.goals.data.GoalStatus.NOT_COMPLETED,
                                date = today,
                                value = 0,
                                reward = suggested.reward,
                            )
                        }
                    onTodayClick(goalsForCallback)
                }
            }
        }
    }
