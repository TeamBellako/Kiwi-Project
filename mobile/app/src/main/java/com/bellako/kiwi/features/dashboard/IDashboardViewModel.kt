package com.bellako.kiwi.features.dashboard

import com.bellako.kiwi.services.common.UIState
import kotlinx.coroutines.flow.StateFlow

interface IDashboardViewModel {
    val state: StateFlow<MetricsState?>
    val uiState: StateFlow<UIState<Unit>>

    suspend fun createMetrics(state: MetricsState): Result<Unit>
    suspend fun updateMetrics(state: MetricsState): Result<Unit>
    suspend fun loadMetrics(state: MetricsState): Result<MetricsDTO>
}