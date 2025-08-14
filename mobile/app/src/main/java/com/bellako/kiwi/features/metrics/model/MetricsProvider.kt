package com.bellako.kiwi.features.metrics.model

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.personality.data.PersonalityState
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object MetricsProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentMetrics(context: Context, localDate: LocalDate, metricsState: MetricsState, personalityState: PersonalityState) : MetricsState {
        val goodTimeSeconds = getUsageTimeForApps(context, localDate, personalityState.goodApps)
        val badTimeSeconds = getUsageTimeForApps(context, localDate, personalityState.badApps)
        return metricsState.copy(date = localDate.toString(), currentGoodTimeSeconds = goodTimeSeconds, currentBadTimeSeconds = badTimeSeconds)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUsageTimeForApps(context: Context, localDate: LocalDate, packageNames: List<String>): Int {
        // The entire day of the date passed
        val zoneId = ZoneId.systemDefault()
        val startTime = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endTime = localDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant().toEpochMilli()

        // Get usage
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        return (usageStatsList
            .filter { usageStats -> packageNames.contains(usageStats.packageName) }
            .sumOf { it.totalTimeInForeground } / 1000).toInt()
    }

}
