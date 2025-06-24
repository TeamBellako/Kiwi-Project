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
    private val todayMetricsDTO: MetricsDTO = MetricsFactory.generateRandomValidMetricDTO(),
    private val pastMetricsDTO: MetricsDTO = MetricsFactory.generateRandomValidMetricDTO(),
    private val futureMetricsDTO: MetricsDTO = MetricsFactory.generateRandomValidMetricDTO(),
    private val currentDate: LocalDate = LocalDate.now()
): ViewModel(), IMetricsViewModel {
    private val _state = MutableStateFlow<MetricsState?>(initialState)
    override val state: StateFlow<MetricsState?> = _state.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Fake exception message")


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createMetrics(state: MetricsState): Result<Unit> {
        _uiState.value = UIState.Loading

        return if (fakeError) {
            _uiState.value = mapExceptionToUIState(fakeException)
            Result.failure(fakeException)
        } else {
            _uiState.value = UIState.Success(Unit)
            _state.value = MetricsMapper.toState(todayMetricsDTO)
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
            _state.value = state
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

            if (LocalDate.parse(date).isEqual(currentDate)) {
                _state.value = MetricsMapper.toState(todayMetricsDTO)
            } else if (LocalDate.parse(date).isAfter(currentDate)) {
                _state.value = MetricsMapper.toState(futureMetricsDTO)
            } else {
                _state.value = MetricsMapper.toState(pastMetricsDTO)
            }

            Result.success(Unit)
        }
    }
}