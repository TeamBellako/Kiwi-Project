package com.bellako.kiwi.features.metrics.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.DateUtils.dateToString
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMetricsByDate(date: LocalDate): Result<MetricsDTO?> =
        runCatching {
            api.getMetricsByDate(dateToString(date))
        }
}
