package com.bellako.kiwi.features.metrics.model

import com.bellako.kiwi.features.metrics.data.MetricsDTO
import java.time.LocalDate

class MetricsRepository(
    private val api: IMetricsAPI,
) {
    suspend fun createMetrics(dto: MetricsDTO): Result<MetricsDTO> =
        runCatching {
            api.createMetrics(dto)
        }

    suspend fun updateMetrics(dto: MetricsDTO): Result<MetricsDTO> =
        runCatching {
            api.updateMetrics(dto)
        }

    suspend fun getMetricsByDate(date: LocalDate): Result<MetricsDTO?> =
        runCatching {
            api.getMetricsByDate(date.toString())
        }
}
