package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.goals.data.AppUsageResult
import com.bellako.kiwi.features.goals.data.GoalDataMapper
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.data.UserAppUsageDTO
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

@HiltViewModel
@Suppress("TooManyFunctions")
class GoalsViewModel
    @Inject
    constructor(
        private val repository: GoalsRepository,
        private val notificationManager: NotificationManager,
        private val usersRepository: UsersRepository,
        private val appUsageProvider: AppUsageProvider,
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

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun updateGoalInCache(updatedGoal: UserGoalStatusDomain) {
            cacheMutex.withLock {
                val keys = cachedGoalsByDate.keys.toList()
                keys.forEach { date ->
                    val goals = cachedGoalsByDate[date] ?: emptyList()
                    val updatedList = goals.map { goal -> if (goal.id == updatedGoal.id) updatedGoal else goal }
                    cachedGoalsByDate[date] = updatedList
                }
                cachedGoalsInProgress = cachedGoalsInProgress?.map { goal -> if (goal.id == updatedGoal.id) updatedGoal else goal }
                cacheDate = getCurrentDate()
                EventBus.emitEvent(EventType.DAILY_GOALS_UPDATED, EventPayload.EmptyPayload())
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun addGoalsToCache(newGoals: List<UserGoalStatusDomain>, date: String) {
            cacheMutex.withLock {
                val existing = cachedGoalsByDate[date]
                cachedGoalsByDate[date] = if (existing == null) newGoals else existing + newGoals
                cacheDate = getCurrentDate()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun createGoals(goals: List<GoalState>): Result<Unit> {
            _state.update { it.copy(isLoading = true, error = null) }
            setUiState(UIState.Loading)

            val result = repository.createGoals(goals.map { UserGoalStatusDataMapper.toDTO(it) })

            return result.fold(
                onSuccess = { resultDTOs ->
                    val newGoalsDomain = resultDTOs.map { UserGoalStatusDataMapper.toDomain(it) }
                    _state.update { currentState ->
                        currentState.copy(
                            goals = resultDTOs.map { UserGoalStatusDataMapper.toState(it) },
                            isLoading = false,
                            error = null
                        )
                    }
                    addGoalsToCache(newGoalsDomain, dateToString(LocalDate.now()))
                    EventBus.emitEvent(EventType.DAILY_GOALS_UPDATED, EventPayload.EmptyPayload())
                    setUiState(UIState.Success(Unit))
                    Result.success(Unit)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                    setUiState(mapExceptionToUIState(throwable))
                    Result.failure(throwable)
                }
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun updateGoalProgress(goalId: Long): Result<UserGoalStatusDomain> {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repository.updateGoalProgress(goalId)

            return result.fold(
                onSuccess = { updatedDTO ->
                    val updatedDomain = UserGoalStatusDataMapper.toDomain(updatedDTO)
                    val updatedState = UserGoalStatusDataMapper.toState(updatedDomain)
                    _state.update { currentState ->
                        val updatedList = currentState.goals.map { if (it.id == updatedState.id) updatedState else it }
                        currentState.copy(goals = updatedList, isLoading = false)
                    }
                    updateGoalInCache(updatedDomain)
                    Result.success(updatedDomain)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                    Result.failure(throwable)
                }
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun updateGoal(goal: UserGoalStatusDomain): Result<UserGoalStatusDomain> {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repository.updateGoal(UserGoalStatusDataMapper.toDTO(goal))

            return result.fold(
                onSuccess = { updatedDTO ->
                    val updatedDomain = UserGoalStatusDataMapper.toDomain(updatedDTO)
                    val updatedState = UserGoalStatusDataMapper.toState(updatedDomain)
                    _state.update { currentState ->
                        val updatedList = currentState.goals.map { if (it.id == updatedState.id) updatedState else it }
                        currentState.copy(goals = updatedList, isLoading = false)
                    }
                    updateGoalInCache(updatedDomain)
                    Result.success(updatedDomain)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                    Result.failure(throwable)
                }
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun completeGoal(goalId: Long): Result<Unit> {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repository.completeGoal(goalId)

            return result.fold(
                onSuccess = { responseDTO ->
                    val updatedDomain = UserGoalStatusDataMapper.toDomain(responseDTO)
                    val updatedState = UserGoalStatusDataMapper.toState(updatedDomain)
                    
                    _state.update { currentState ->
                        val updatedList = currentState.goals.map { if (it.id == updatedState.id) updatedState else it }
                        currentState.copy(goals = updatedList, isLoading = false)
                    }

                    if (updatedDomain.onCompletedEvent != "_") {
                        try {
                            val eventName = updatedDomain.onCompletedEvent.uppercase().trim()
                            val eventType = EventType.valueOf(eventName)
                            EventBus.emitEvent(eventType, EventPayload.EntityIdPayload(updatedDomain.onCompletedEntityId))
                        } catch (e: Exception) {
                            Log.e("GoalsViewModel", "Event emission failed for: ${updatedDomain.onCompletedEvent}", e)
                        }
                    }

                    updateGoalInCache(updatedDomain)
                    usersRepository.getMyUserPoints()
                    Result.success(Unit)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                    Result.failure(throwable)
                }
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun uncompleteGoal(goalId: Long): Result<Unit> {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repository.uncompleteGoal(goalId)

            return result.fold(
                onSuccess = { responseDTO ->
                    val updatedDomain = UserGoalStatusDataMapper.toDomain(responseDTO)
                    val updatedState = UserGoalStatusDataMapper.toState(updatedDomain)
                    _state.update { currentState ->
                        val updatedList = currentState.goals.map { if (it.id == updatedState.id) updatedState else it }
                        currentState.copy(goals = updatedList, isLoading = false)
                    }
                    updateGoalInCache(updatedDomain)
                    Result.success(Unit)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                    Result.failure(throwable)
                }
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun getGoalsByDate(date: String): Result<List<UserGoalStatusDomain>> {
            cacheMutex.withLock {
                if (cachedGoalsByDate.containsKey(date) && cacheDate == getCurrentDate()) {
                    return Result.success(cachedGoalsByDate[date]!!)
                }
            }

            val result = repository.getGoalsByDate(stringToDate(date))
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
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repository.getAllGoals()
            return result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false) }
                    Result.success(Unit)
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                    Result.failure(throwable)
                }
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun getGoalsInProgress(): Result<List<UserGoalStatusDomain>> {
            cacheMutex.withLock {
                if (cachedGoalsInProgress != null && cacheDate == getCurrentDate()) {
                    return Result.success(cachedGoalsInProgress!!)
                }
            }

            return repository.getGoalsInProgress().map { goalDTOs ->
                val domainGoals = goalDTOs.map { UserGoalStatusDataMapper.toDomain(it) }
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

            return repository.getGoalDefinitions().map { goalDTOs ->
                val goalDomains = goalDTOs.map { GoalDataMapper.toDomain(it) }
                cacheMutex.withLock { cachedGoalDefinitions = goalDomains }
                goalDomains
            }
        }

        override suspend fun invalidateGoalsInProgressCache() {
            cacheMutex.withLock { cachedGoalsInProgress = null }
        }

        override fun onCleared() {
            super.onCleared()
            cachedGoalsByDate.clear()
            cachedGoalsInProgress = null
            cachedGoalDefinitions = null
            cacheDate = null
        }

        fun notifyNewGoals(goals: List<IGoal>) = notificationManager.notify(NotificationEvent.Goal(GoalNotificationType.NEW, goals))
        fun notifyYesterdayGoals(goals: List<IGoal>) = notificationManager.notify(NotificationEvent.Goal(GoalNotificationType.YESTERDAY, goals))

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun checkAndNotifyGoals() {
            autoReviewAppUsageGoals()
            val inProgress = getGoalsInProgress().getOrNull()
            if (!inProgress.isNullOrEmpty()) {
                notifyYesterdayGoals(inProgress)
                return
            }
            if (getGoalsByDate(dateToString(LocalDate.now())).getOrNull().isNullOrEmpty()) {
                getGoalDefinitions().getOrNull()?.let {
                    createGoalsFromDefinitions(it)
                    notifyNewGoals(it)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun getDailyGoalsProgress(date: String): Float {
            val goals = getGoalsByDate(date).getOrElse { return 0f }
            if (goals.isEmpty()) return 0f
            return goals.map { if (it.target <= 0) 0f else (it.value.toFloat() / it.target).coerceIn(0f, 1f) }.average().toFloat()
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
        override suspend fun getAppsAverageUsage(goodApps: List<String>, badApps: List<String>): Result<AppUsageResult> =
            runCatching { AppUsageResult(appUsageProvider.getAverageWeeklyUsage(goodApps), appUsageProvider.getAverageWeeklyUsage(badApps)) }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
        override suspend fun saveBaselineAppUsage(goodApps: List<String>, badApps: List<String>): Result<UserAppUsageDTO> =
            runCatching {
                val dto = UserAppUsageDTO(
                    appUsageProvider.getAverageWeeklyUsage(goodApps).sumOf { it.averageDailyUsageMs },
                    appUsageProvider.getAverageWeeklyUsage(badApps).sumOf { it.averageDailyUsageMs }
                )
                repository.saveAppUsageBaseline(dto).getOrThrow()
            }

        override suspend fun autoReviewAppUsageGoals(): Result<Unit> = runCatching { repository.autoReviewAppUsageGoals().getOrThrow(); Unit }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun createGoalsFromDefinitions(goalDefinitions: List<GoalDomain>): Result<Unit> {
            if (goalDefinitions.isEmpty()) return Result.failure(IllegalArgumentException("Empty list"))
            val today = dateToString(LocalDate.now())
            cacheMutex.withLock { cachedGoalDefinitions = null; cachedGoalsByDate.remove(today) }
            return createGoals(goalDefinitions.map { GoalDataMapper.toUserGoalStatusState(it, today) })
        }
    }
