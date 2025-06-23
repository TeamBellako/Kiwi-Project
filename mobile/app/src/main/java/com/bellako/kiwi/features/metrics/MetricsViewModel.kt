package com.bellako.kiwi.features.metrics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.bellako.kiwi.features.users.Email
import com.bellako.kiwi.services.common.HTTPUtils.extractHttpExceptionMessage
import com.bellako.kiwi.services.common.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeParseException

@HiltViewModel
class MetricsViewModel @Inject constructor(
    private val repository: MetricsRepository
) : ViewModel(), IMetricsViewModel {
    private val _state = MutableStateFlow<MetricsState?>(MetricsState("", "", 0, 0))
    override val state: StateFlow<MetricsState?> = _state.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()


    override suspend fun createMetrics(state: MetricsState): Result<Unit> {
        val domain = validateAndMapToDomain(state) ?: return failureWithError(invalidDataMessage())

        _uiState.value = UIState.Loading

        val exists = repository.getMetricsByDateAndUser(domain.email, domain.date).getOrNull()
        if (exists != null) return failureWithError("A metrics entry already exists with that user and date")

        return handleResult(repository.createMetrics(MetricsMapper.toDTO(domain)))
    }

    override suspend fun updateMetrics(state: MetricsState): Result<Unit> {
        val domain = validateAndMapToDomain(state) ?: return failureWithError(invalidDataMessage())

        _uiState.value = UIState.Loading

        val existing = repository.getMetricsByDateAndUser(domain.email, domain.date).getOrNull()
        if (existing == null) return failureWithError("There is no metrics entry with that user and date")

        return handleResult(repository.updateMetrics(MetricsMapper.toDTO(domain)))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun loadMetrics(email: String, date: String): Result<MetricsDTO> {
        val parsedEmail = Email.of(email).getOrNull() ?: return failureWithError(invalidDataMessage())

        val parsedDate = try {
            LocalDate.parse(date)
        } catch (e: DateTimeParseException) {
            return failureWithError(invalidDataMessage())
        }

        _uiState.value = UIState.Loading

        return repository.getMetricsByDateAndUser(parsedEmail, parsedDate).fold(
            onSuccess = { dto ->
                _uiState.value = UIState.Success(Unit)
                Result.success(dto ?: MetricsDTO(parsedEmail.value, parsedDate.toString(), 0, 0))
            },
            onFailure = { throwable -> failureWithMappedError(throwable) }
        )
    }


    private fun validateAndMapToDomain(state: MetricsState): Metrics? {
        return MetricsMapper.toDomain(state).getOrNull()
    }

    private fun invalidDataMessage(): String = """
        Invalid metrics. Metrics must:
        - Have a valid email format
        - Have a positive number of steps
        - Have a positive number of screenTimeSeconds
    """.trimIndent()

    private fun <T> failureWithError(message: String): Result<T> {
        _uiState.value = UIState.Error(message)
        return Result.failure(Exception(message))
    }

    private fun <T> failureWithMappedError(throwable: Throwable): Result<T> {
        _uiState.value = when (throwable) {
            is HttpException -> {
                if (throwable.code() >= 500) UIState.GeneralError
                else UIState.Error(extractHttpExceptionMessage(throwable))
            }
            else -> UIState.GeneralError
        }
        return Result.failure(throwable)
    }

    private fun handleResult(result: Result<Unit>): Result<Unit> {
        return result.fold(
            onSuccess = {
                _uiState.value = UIState.Success(Unit)
                Result.success(Unit)
            },
            onFailure = { throwable -> failureWithMappedError(throwable) }
        )
    }
}