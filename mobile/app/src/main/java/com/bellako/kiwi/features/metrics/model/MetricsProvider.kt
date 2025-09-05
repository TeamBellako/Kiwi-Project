package com.bellako.kiwi.features.metrics.model

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.personality.data.PersonalityState
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object MetricsProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDeviceMetrics(
        context: Context,
        metricsState: MetricsState,
        personalityState: PersonalityState,
    ): MetricsState {
        val date = stringToDate(metricsState.date)
        val goodTimeSeconds = getUsageTimeForApps(context, date, personalityState.goodApps)
        val badTimeSeconds = getUsageTimeForApps(context, date, personalityState.badApps)
        return metricsState.copy(
            date = metricsState.date,
            currentGoodTimeSeconds = goodTimeSeconds,
            currentBadTimeSeconds = badTimeSeconds,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDayLocalTime(
        date: LocalDate,
        localTime: LocalTime,
    ): Long =
        date
            .atTime(localTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUsageTimeForApps(
        context: Context,
        date: LocalDate,
        packageNames: List<String>,
    ): Int {
        val startTime = getDayLocalTime(date, LocalTime.MIN)
        val endTime = getDayLocalTime(date, LocalTime.MAX)

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        return (
            usageStatsList
                .filter { usageStats -> packageNames.contains(usageStats.packageName) }
                .sumOf { it.totalTimeInForeground } / 1000
        ).toInt()
    }
}
