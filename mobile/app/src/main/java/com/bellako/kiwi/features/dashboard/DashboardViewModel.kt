package com.bellako.kiwi.features.dashboard

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.services.common.HTTPUtils.extractHttpExceptionMessage
import com.bellako.kiwi.services.common.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: MetricsRepository,
) : ViewModel(), IDashboardViewModel {
    private val _state = MutableStateFlow<MetricsState?>(MetricsState("", "", 0, 0))
    override val state: StateFlow<MetricsState?> = _state.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    override suspend fun createMetrics(state: MetricsState): Result<Unit> {
        val domainResult = MetricsMapper.toDomain(state)
        if (domainResult.isFailure) {
            val message = getInvalidDataMessage()
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }

        val domain = domainResult.getOrThrow()
        _uiState.value = UIState.Loading

        val existingMetrics = repository.getMetricsByDateAndUser(domain.email.value, domain.date.toString()).getOrNull()
        if (existingMetrics != null) {
            val message = "A metrics entry already exists with that user and date"
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message));
        }

        val result = repository.createMetrics(MetricsMapper.toDTO(domain))
        return result.fold(
            onSuccess = {
                _uiState.value = UIState.Success(Unit)
                Result.success(Unit)
            },
            onFailure = { throwable ->
                _uiState.value = when (throwable) {
                    is HttpException -> {
                        if (throwable.code() >= 500) UIState.GeneralError
                        else UIState.Error(extractHttpExceptionMessage(throwable))
                    }
                    else -> UIState.GeneralError
                }
                Result.failure(throwable)
            }
        )
    }

    override suspend fun updateMetrics(state: MetricsState): Result<Unit> {
        val domainResult = MetricsMapper.toDomain(state)
        if (domainResult.isFailure) {
            val message = getInvalidDataMessage()
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }

        val domain = domainResult.getOrThrow()
        _uiState.value = UIState.Loading

        val existingMetrics = repository.getMetricsByDateAndUser(domain.email.value, domain.date.toString()).getOrNull()
        if (existingMetrics == null) {
            val message = "There is no metrics entry with that user and date"
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message));
        }

        val result = repository.updateMetrics(MetricsMapper.toDTO(domain))
        return result.fold(
            onSuccess = {
                _uiState.value = UIState.Success(Unit)
                Result.success(Unit)
            },
            onFailure = { throwable ->
                _uiState.value = when (throwable) {
                    is HttpException -> {
                        if (throwable.code() >= 500) UIState.GeneralError
                        else UIState.Error(extractHttpExceptionMessage(throwable))
                    }
                    else -> UIState.GeneralError
                }
                Result.failure(throwable)
            }
        )
    }

    override suspend fun loadMetrics(state: MetricsState): Result<MetricsDTO> {
        val domainResult = MetricsMapper.toDomain(state)
        if (domainResult.isFailure) {
            val message = getInvalidDataMessage()
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }

        val domain = domainResult.getOrThrow()
        _uiState.value = UIState.Loading

        val result = repository.getMetricsByDateAndUser(domain.email.value, domain.date.toString())
        return result.fold(
            onSuccess = {
                _uiState.value = UIState.Success(Unit)

                val resultMetricsDTO = result.getOrNull()
                if (resultMetricsDTO == null) {
                    val defaultMetricsDTO: MetricsDTO = MetricsDTO(domain.email.value, domain.date.toString(), 0, 0)
                    Result.success(defaultMetricsDTO)
                }
                Result.success(resultMetricsDTO!!)
            },
            onFailure = { throwable ->
                _uiState.value = when (throwable) {
                    is HttpException -> {
                        if (throwable.code() >= 500) UIState.GeneralError
                        else UIState.Error(extractHttpExceptionMessage(throwable))
                    }
                    else -> UIState.GeneralError
                }
                Result.failure(throwable)
            }
        )
    }

    private fun getInvalidDataMessage(): String {
        return """
            Invalid metrics. Metrics must:
            - Have a valid email format
            - Have a positive number of steps
            - Have a positive number of screenTimeSeconds
        """.trimIndent()
    }
}