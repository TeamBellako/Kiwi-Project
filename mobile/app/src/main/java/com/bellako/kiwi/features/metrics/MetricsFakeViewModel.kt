package com.bellako.kiwi.features.metrics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.bellako.kiwi.services.common.HTTPUtils.mapExceptionToUIState
import com.bellako.kiwi.services.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
class MetricsFakeViewModel  constructor(
    initialState: MetricsState,
    private val fakeNewMetricsDTO: MetricsDTO = MetricsFactory.generateRandomValidMetricDTO(),
    private val fakePastMetricsDTO: MetricsDTO = MetricsFactory.generateRandomValidMetricDTO(),
    private val currentDate: LocalDate = LocalDate.now()
): ViewModel(), IMetricsViewModel {
    private val _state = MutableStateFlow<MetricsState?>(initialState)
    override val state: StateFlow<MetricsState?> = _state.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    var fakeError: Boolean = false
    var fakeNonExistingMetrics: Boolean = false
    var fakeException: Exception = Exception("Fake exception message")


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createMetrics(state: MetricsState): Result<Unit> {
        _uiState.value = UIState.Loading

        return if (fakeError) {
            _uiState.value = mapExceptionToUIState(fakeException)
            Result.failure(fakeException)
        } else {
            _uiState.value = UIState.Success(Unit)
            _state.value = MetricsMapper.toState(fakeNewMetricsDTO.copy(date = currentDate.toString()))
            Result.success(Unit)
        }
    }

    override suspend fun updateMetrics(state: MetricsState): Result<Unit> {
        _uiState.value = UIState.Loading

        return if (fakeError) {
            _uiState.value = mapExceptionToUIState(fakeException)
            Result.failure(fakeException)
        } else {
            _uiState.value = UIState.Success(Unit)
            Result.success(Unit)
        }
    }

    override suspend fun loadMetrics(
        email: String,
        date: String
    ): Result<Unit> {
        _uiState.value = UIState.Loading

        return if (fakeError) {
            _uiState.value = mapExceptionToUIState(fakeException)

            Result.failure(fakeException)
        } else {
            _uiState.value = UIState.Success(Unit)

            if (fakeNonExistingMetrics) {
                _state.value = _state.value?.copy(steps = 0, screenTimeSeconds = 0)
            } else {
                _state.value = MetricsMapper.toState(fakePastMetricsDTO)
            }

            Result.success(Unit)
        }
    }
}