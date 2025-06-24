package com.bellako.kiwi.features.metrics

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

object MetricsReader {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMetrics() : Metrics {
        // TODO

        val randomMetricsDTO = MetricsFactory.generateRandomValidMetricDTO()
        return MetricsMapper.toDomain(randomMetricsDTO.copy(date = LocalDate.now().toString())).getOrNull()!!
    }

}