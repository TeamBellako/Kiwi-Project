package com.bellako.kiwi.features.metrics

import com.bellako.kiwi.features.users.Email
import com.bellako.kiwi.services.common.UIState
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface IMetricsViewModel {
    val state: StateFlow<MetricsState?>
    val uiState: StateFlow<UIState<Unit>>

    suspend fun createMetrics(state: MetricsState): Result<Unit>
    suspend fun updateMetrics(state: MetricsState): Result<Unit>
    suspend fun loadMetrics(email: String, date: String): Result<MetricsDTO>
}