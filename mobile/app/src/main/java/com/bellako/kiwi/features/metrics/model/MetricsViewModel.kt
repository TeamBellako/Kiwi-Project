package com.bellako.kiwi.features.metrics.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.metrics.data.MetricsDataMapper
import com.bellako.kiwi.features.metrics.data.MetricsState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

@HiltViewModel
class MetricsViewModel
    @Inject
    constructor(
        private val repository: MetricsRepository,
    ) : BaseViewModel(),
        IMetricsViewModel {
        private val _state =
            MutableStateFlow(
                MetricsState(
                    date = "",
                    maxGoodTimeSeconds = 0,
                    currentGoodTimeSeconds = 0,
                    maxBadTimeSeconds = 0,
                    currentBadTimeSeconds = 0,
                ),
            )
        override val state: StateFlow<MetricsState> = _state.asStateFlow()

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDateChanged(newDate: LocalDate) {
            _state.value = _state.value.copy(date = dateToString(newDate))
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun createMetrics(state: MetricsState): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            val domain = MetricsDataMapper.toDomain(state).copy(currentGoodTimeSeconds = 0, currentBadTimeSeconds = 0)
            val result = repository.createMetrics(MetricsDataMapper.toDTO(domain))
            setIsLoading(false)
            setUiState(UIState.Idle)
            return handleResult(result) {
                _state.value = MetricsDataMapper.toState(result.getOrNull()!!)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun updateMetrics(state: MetricsState): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            val domain = MetricsDataMapper.toDomain(state)
            val result = repository.updateMetrics(MetricsDataMapper.toDTO(domain))
            setIsLoading(false)
            setUiState(UIState.Idle)
            return handleResult(result) {
                _state.value = MetricsDataMapper.toState(result.getOrNull()!!)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun loadMetrics(date: String): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            val result = repository.getMetricsByDate(stringToDate(date))
            setIsLoading(false)
            setUiState(UIState.Idle)
            return handleResult(result) {
                _state.value = MetricsDataMapper.toState(result.getOrNull()!!)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun getAppUsageProgress(date: String): Float {
            val dto = repository.getMetricsByDate(stringToDate(date)).getOrNull() ?: return 0f
            return MetricsDataMapper.toState(dto).getAppUsageProgress()
        }
    }
