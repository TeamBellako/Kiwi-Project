package com.bellako.kiwi.features.metrics

import com.bellako.kiwi.features.common.IBaseViewModel

interface IMetricsViewModel : IBaseViewModel<MetricsState> {
    suspend fun createMetrics(state: MetricsState): Result<Unit>
    suspend fun updateMetrics(state: MetricsState): Result<Unit>
    suspend fun loadMetrics(date: String): Result<Unit>
}
