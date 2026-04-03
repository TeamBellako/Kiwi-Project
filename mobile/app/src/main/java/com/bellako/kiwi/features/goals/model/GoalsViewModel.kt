package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.goals.data.GoalDataMapper
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.data.UserGoalStatusDataMapper
import com.bellako.kiwi.features.goals.data.UserGoalStatusDomain
import com.bellako.kiwi.features.goals.screens.GoalNotificationType
import com.bellako.kiwi.features.notifications.controller.NotificationEvent
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.users.model.UsersRepository
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
        private val usersRepository: UsersRepository,
    ) : BaseViewModel(),
        IGoalsViewModel {
        private val _state = MutableStateFlow(GoalsListState())
        override val state: StateFlow<GoalsListState> = _state.asStateFlow()

        // Cache de goals por fecha
        private val cachedGoalsByDate = mutableMapOf<String, List<UserGoalStatusDomain>>()
        private var cachedGoalsInProgress: List<UserGoalStatusDomain>? = null
        private var cachedGoalDefinitions: List<GoalDomain>? = null

        private var cacheDate: String? = null

        // Mutex para proteger el acceso concurrente a la cache
        private val cacheMutex = Mutex()

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getCurrentDate(): String = dateToString(LocalDate.now())

        /**
         * Actualiza un goal en todas las entradas del cache donde aparezca
         * @param updatedGoal El goal actualizado
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun updateGoalInCache(updatedGoal: UserGoalStatusDomain) {
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
            newGoals: List<UserGoalStatusDomain>,
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

            val goalsDTO = goals.map { UserGoalStatusDataMapper.toDTO(it) }
            val result = repository.createGoals(goalsDTO)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                val resultDTOs = result.getOrNull()!!
                _state.value =
                    _state.value.copy(
                        goals = resultDTOs.map { UserGoalStatusDataMapper.toState(it) },
                        isLoading = false,
                        error = null,
                    )
                val newGoalsDomain = resultDTOs.map { UserGoalStatusDataMapper.toDomain(it) }
                val today = dateToString(LocalDate.now())
                addGoalsToCache(newGoalsDomain, today)

                EventBus.emitEvent(EventType.DAILY_GOALS_UPDATED, EventPayload.EmptyPayload())
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
        override suspend fun updateGoalProgress(goalId: Long): Result<UserGoalStatusDomain> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.updateGoalProgress(goalId)

            setIsLoading(false)
            setUiState(UIState.Idle)

            // Manejar explícitamente para poder usar suspend dentro del flujo
            return if (result.isSuccess) {
                val updatedDTO = result.getOrNull()!!
                val updatedState = UserGoalStatusDataMapper.toState(updatedDTO)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedState.id) updatedState else it }
                _state.value = _state.value.copy(goals = updatedGoals, isLoading = false, error = null)
                val updatedDomain = UserGoalStatusDataMapper.toDomain(updatedDTO)
                updateGoalInCache(updatedDomain)
                Result.success(updatedDomain)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun updateGoal(goal: UserGoalStatusDomain): Result<UserGoalStatusDomain> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.updateGoal(UserGoalStatusDataMapper.toDTO(goal))

            setIsLoading(false)
            setUiState(UIState.Idle)

            return if (result.isSuccess) {
                val updatedDTO = result.getOrNull()!!
                val updatedState = UserGoalStatusDataMapper.toState(updatedDTO)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedState.id) updatedState else it }

                _state.value = _state.value.copy(goals = updatedGoals, isLoading = false, error = null)

                val updatedDomain = UserGoalStatusDataMapper.toDomain(updatedDTO)

                updateGoalInCache(updatedDomain)

                EventBus.emitEvent(EventType.DAILY_GOALS_UPDATED, EventPayload.EmptyPayload())

                Result.success(updatedDomain)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun createGoalsFromDefinitions(goalDefinitions: List<GoalDomain>): Result<Unit> {
            if (goalDefinitions.isEmpty()) {
                return Result.failure(IllegalArgumentException("La lista de sugerencias está vacía"))
            }
            val today = dateToString(LocalDate.now())
            val goalsToCreate = goalDefinitions.map { GoalDataMapper.toUserGoalStatusState(it, today) }
            cacheMutex.withLock {
                cachedGoalDefinitions = null
                cachedGoalsByDate.remove(today)
            }
            return createGoals(goalsToCreate)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun completeGoal(goalId: Long): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.completeGoal(goalId)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                val updatedGoal = UserGoalStatusDataMapper.toState(result.getOrNull()!!)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedGoal.id) updatedGoal else it }
                _state.value = _state.value.copy(goals = updatedGoals, isLoading = false, error = null)

                val updatedDomain = UserGoalStatusDataMapper.toDomain(result.getOrNull()!!)

                EventBus.emitEvent(EventType.DAILY_GOALS_UPDATED, EventPayload.EmptyPayload())

                updateGoalInCache(updatedDomain)
                usersRepository.getMyUserPoints() // Refrescar puntos al completar goal
            }.also {
                if (it.isFailure) {
                    _state.value = _state.value.copy(isLoading = false, error = it.exceptionOrNull()?.message)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun uncompleteGoal(goalId: Long): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.uncompleteGoal(goalId)

            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                val updatedGoal = UserGoalStatusDataMapper.toState(result.getOrNull()!!)
                val updatedGoals = _state.value.goals.map { if (it.id == updatedGoal.id) updatedGoal else it }
                _state.value = _state.value.copy(goals = updatedGoals, isLoading = false, error = null)
                val updatedDomain = UserGoalStatusDataMapper.toDomain(result.getOrNull()!!)
                updateGoalInCache(updatedDomain)
            }.also {
                if (it.isFailure) {
                    _state.value = _state.value.copy(isLoading = false, error = it.exceptionOrNull()?.message)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun getGoalsByDate(date: String): Result<List<UserGoalStatusDomain>> {
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
                val domainGoals = goalDTOs?.map { UserGoalStatusDataMapper.toDomain(it) } ?: emptyList()
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

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun getGoalsInProgress(): Result<List<UserGoalStatusDomain>> {
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
                val domainGoals = goalDTOs.map { UserGoalStatusDataMapper.toDomain(it) }
                // Guardar en cache
                cacheMutex.withLock {
                    cachedGoalsInProgress = domainGoals
                    cacheDate = getCurrentDate()
                }
                domainGoals
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun getGoalDefinitions(): Result<List<GoalDomain>> {
            cacheMutex.withLock {
                if (cachedGoalDefinitions != null && cacheDate == getCurrentDate()) {
                    return Result.success(cachedGoalDefinitions!!)
                }
            }

            setIsLoading(true)
            setUiState(UIState.Loading)

            val result = repository.getGoalDefinitions()

            setIsLoading(false)
            setUiState(UIState.Idle)

            return result.map { goalDTOs ->
                val goalDomains = goalDTOs.map { GoalDataMapper.toDomain(it) }
                cacheMutex.withLock {
                    cachedGoalDefinitions = goalDomains
                }
                goalDomains
            }
        }

        // Limpiar cache cuando se destruye el ViewModel
        override fun onCleared() {
            super.onCleared()
            cachedGoalsByDate.clear()
            cachedGoalsInProgress = null
            cachedGoalDefinitions = null
            cacheDate = null
        }

        fun notifyNewGoals(goals: List<IGoal>) {
            notificationManager.notify(
                NotificationEvent.Goal(
                    type = GoalNotificationType.NEW,
                    goals = goals,
                ),
            )
        }

        fun notifyYesterdayGoals(goals: List<IGoal>) {
            notificationManager.notify(
                NotificationEvent.Goal(
                    type = GoalNotificationType.YESTERDAY,
                    goals = goals,
                ),
            )
        }

        /**
         * Evalúa si hay notificaciones de goals que mostrar y las envía automáticamente.
         * 1. Verifica si hay goals de ayer en progreso (muestra primero)
         * 2. Verifica si hay goals de hoy o sugerencias (muestra después)
         */
        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun checkAndNotifyGoals() {
            val today = dateToString(LocalDate.now())

            val inProgressResult = getGoalsInProgress()
            val yesterdayGoals = inProgressResult.getOrNull()

            if (!yesterdayGoals.isNullOrEmpty()) {
                notifyYesterdayGoals(yesterdayGoals)
                return // Salir para mostrar solo la de ayer primero
            }

            val todayResult = getGoalsByDate(today)
            val todayGoals = todayResult.getOrNull()

            if (!todayGoals.isNullOrEmpty()) {
                return
            }

            val goalDefinitionsResult = getGoalDefinitions()
            val goalDefinitions = goalDefinitionsResult.getOrNull()

            if (!goalDefinitions.isNullOrEmpty()) {
                notifyNewGoals(goalDefinitions)
            }
        }
    }
