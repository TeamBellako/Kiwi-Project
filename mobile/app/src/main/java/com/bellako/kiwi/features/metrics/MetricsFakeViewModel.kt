package com.bellako.kiwi.features.metrics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.bellako.kiwi.services.common.HTTPUtils.mapExceptionToUIState
import com.bellako.kiwi.services.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MetricsFakeViewModel(
    initialState: MetricsState,
): ViewModel(), IMetricsViewModel {
    private val _state = MutableStateFlow<MetricsState?>(initialState)
    override val state: StateFlow<MetricsState?> = _state.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    var fakeError: Boolean = false
    var fakeDefaultQueryResult: Boolean = false
    var fakeException: Exception = Exception("Fake exception message")

    override suspend fun createMetrics(state: MetricsState): Result<Unit> { return fakeCommand() }

    override suspend fun updateMetrics(state: MetricsState): Result<Unit> { return fakeCommand() }

    override suspend fun loadMetrics(
        email: String,
        date: String
    ): Result<MetricsDTO> {
        return fakeQuery(email, date)
    }

    private fun fakeCommand() : Result<Unit> {
        _uiState.value = UIState.Loading

        return if (fakeError) {
            _uiState.value = mapExceptionToUIState(fakeException)
            Result.failure(fakeException)
        } else {
            _uiState.value = UIState.Success(Unit)
            Result.success(Unit)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fakeQuery(
        email: String,
        date: String
    ): Result<MetricsDTO> {
        _uiState.value = UIState.Loading

        return if (fakeError) {
            _uiState.value = mapExceptionToUIState(fakeException)
            Result.failure(fakeException)
        } else {
            _uiState.value = UIState.Success(Unit)

            val result: MetricsDTO = if (fakeDefaultQueryResult) {
                MetricsDTO(email, date, 0, 0)
            } else {
                MetricsFactory.generateRandomValidMetricDTO()
            }

            Result.success(result)
        }
    }
}