package com.bellako.kiwi.features.metrics.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.metrics.data.Metrics
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.common.model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeParseException

@HiltViewModel
class MetricsViewModel @Inject constructor(
    private val repository: MetricsRepository
) : BaseViewModel(), IMetricsViewModel {
    private val _state = MutableStateFlow(MetricsState(date = "", maxGoodTimeSeconds = 0, currentGoodTimeSeconds = 0, maxBadTimeSeconds = 0, currentBadTimeSeconds = 0))
    override val state: StateFlow<MetricsState> = _state.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createMetrics(state: MetricsState): Result<Unit> {
        val domain = validateAndMapToDomain(state) ?: return failureWithError(invalidDataMessage())

        val exists = repository.getMetricsByDate(domain.date).getOrNull()
        if (exists != null) return failureWithError("A metrics entry already exists with that user and date")

        return handleResult(
            repository.createMetrics(MetricsMapper.toDTO(domain))
        ) {
            _state.value = MetricsMapper.toState(domain)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateMetrics(state: MetricsState): Result<Unit> {
        val domain = validateAndMapToDomain(state) ?: return failureWithError(invalidDataMessage())

        val existing = repository.getMetricsByDate(domain.date).getOrNull()
        if (existing == null) return failureWithError("There is no metrics entry with that user and date")

        return handleResult(
            repository.updateMetrics(MetricsMapper.toDTO(domain))
        ) {
            _state.value = MetricsMapper.toState(domain)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun loadMetrics(date: String): Result<Unit> {
        val parsedDate = try {
            LocalDate.parse(date)
        } catch (_: DateTimeParseException) {
            return failureWithError(invalidDataMessage())
        }

        _state.value = MetricsState(date = "", maxGoodTimeSeconds = 0, currentGoodTimeSeconds = 0, maxBadTimeSeconds = 0, currentBadTimeSeconds = 0)

        val result = repository.getMetricsByDate(parsedDate).getOrNull()
        if (result == null) {
            return Result.failure(Exception("No metrics found"))
        }

        _state.value = MetricsMapper.toState(result)
        return Result.success(Unit)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateAndMapToDomain(state: MetricsState): Metrics? {
        return MetricsMapper.toDomain(state)
    }

    private fun invalidDataMessage(): String = "Invalid metrics. Metrics must have a positive number of good and bad apps TimeSeconds".trimIndent()

}
