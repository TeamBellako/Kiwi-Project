package com.bellako.kiwi.features.metrics.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.metrics.data.MetricsState
import java.time.LocalDate

interface IMetricsViewModel : IBaseViewModel<MetricsState> {
    fun onDateChanged(newDate: LocalDate)

    suspend fun createMetrics(state: MetricsState): Result<Unit>

    suspend fun updateMetrics(state: MetricsState): Result<Unit>

    suspend fun loadMetrics(date: String): Result<Unit>
}
