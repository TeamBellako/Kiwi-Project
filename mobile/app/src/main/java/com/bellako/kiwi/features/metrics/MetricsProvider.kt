package com.bellako.kiwi.features.metrics

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.services.common.Logger
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object MetricsProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMetrics(context: Context, localDate: LocalDate) : Metrics? {
        val steps = 0
        val screenTimeSeconds = getScreenTimeInSeconds(context, localDate)

        val metricsDTO = MetricsDTO(localDate.toString(), steps, screenTimeSeconds)
        return MetricsMapper.toDomain(metricsDTO).getOrNull()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getScreenTimeInSeconds(context: Context, localDate: LocalDate): Int {
        val zoneId = ZoneId.systemDefault()

        val startOfDayMillis = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDayMillis = localDate
            .atTime(LocalTime.MAX)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startOfDayMillis,
            endOfDayMillis
        )

        if (usageStatsList.isNullOrEmpty()) {
            Logger.warn("No usage stats available. Permission may not be granted")
            return -1
        }

        return ((usageStatsList.sumOf { it.totalTimeInForeground }) / 1000).toInt()
    }
}