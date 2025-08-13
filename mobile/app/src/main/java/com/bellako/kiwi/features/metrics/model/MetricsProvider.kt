package com.bellako.kiwi.features.metrics.model

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.Logger
import com.bellako.kiwi.features.metrics.data.MetricsState
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object MetricsProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMetrics(context: Context, localDate: LocalDate, state: MetricsState) : MetricsState {
        val badTimeSeconds = getBadTimeSeconds(context, localDate)
        val goodTimeSeconds = getGoodTimeSeconds(context, localDate)
        return state.copy(date = localDate.toString(), currentGoodTimeSeconds = goodTimeSeconds, currentBadTimeSeconds = badTimeSeconds)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getGoodTimeSeconds(context: Context, localDate: LocalDate): Int {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBadTimeSeconds(context: Context, localDate: LocalDate): Int {
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