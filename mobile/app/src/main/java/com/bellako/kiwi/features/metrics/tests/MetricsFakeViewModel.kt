package com.bellako.kiwi.features.metrics.tests

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.metrics.data.MetricsDTO
import com.bellako.kiwi.features.metrics.data.MetricsDataMapper
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.metrics.model.MetricsFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MetricsFakeViewModel(
    initialState: MetricsState,
    private val todayMetricsDTO: MetricsDTO = MetricsFactory.generateRandomValidMetricDTO(),
    private val pastMetricsDTO: MetricsDTO = MetricsFactory.generateRandomValidMetricDTO(),
    private val futureMetricsDTO: MetricsDTO = MetricsFactory.generateRandomValidMetricDTO(),
    private val currentDate: LocalDate = LocalDate.now(),
) : BaseFakeViewModel(),
    IMetricsViewModel {
    private val _state = MutableStateFlow<MetricsState?>(initialState)
    override val state: StateFlow<MetricsState?> = _state.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Fake exception message")

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createMetrics(state: MetricsState): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            _state.value = MetricsDataMapper.toState(todayMetricsDTO)
            Result.success(Unit)
        }

    override suspend fun updateMetrics(state: MetricsState): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            _state.value = MetricsDataMapper.toState(todayMetricsDTO)
            Result.success(Unit)
        }

    override suspend fun loadMetrics(date: String): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()

            when {
                LocalDate.parse(date).isEqual(currentDate) -> {
                    _state.value = MetricsDataMapper.toState(todayMetricsDTO)
                }
                LocalDate.parse(date).isAfter(currentDate) -> {
                    _state.value = MetricsDataMapper.toState(futureMetricsDTO)
                }
                else -> {
                    _state.value = MetricsDataMapper.toState(pastMetricsDTO)
                }
            }

            Result.success(Unit)
        }
}
