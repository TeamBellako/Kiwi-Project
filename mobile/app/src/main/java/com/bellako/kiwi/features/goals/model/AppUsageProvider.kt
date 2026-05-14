package com.bellako.kiwi.features.goals.model

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.goals.data.AppUsageStats
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

private const val DAYS_IN_WEEK = 7L
private const val MS_PER_DAY = 24 * 60 * 60 * 1000L

@Singleton
class AppUsageProvider
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Queries the last 7 days of foreground usage for each given package and returns
         * the average daily usage in milliseconds.
         *
         * Requires PACKAGE_USAGE_STATS permission granted by the user.
         */
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
        fun getAverageWeeklyUsage(packageNames: List<String>): List<AppUsageStats> {
            if (packageNames.isEmpty()) return emptyList()

            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                    ?: return packageNames.map { AppUsageStats(it, 0L) }

            val endTime = System.currentTimeMillis()
            val startTime = endTime - DAYS_IN_WEEK * MS_PER_DAY

            val stats =
                usageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
                    .orEmpty()

            // Sum total foreground time per package across all daily entries
            val totalByPackage = mutableMapOf<String, Long>()
            for (entry in stats) {
                val pkg = entry.packageName
                if (pkg in packageNames) {
                    totalByPackage[pkg] = (totalByPackage[pkg] ?: 0L) + entry.totalTimeInForeground
                }
            }

            return packageNames.map { pkg ->
                AppUsageStats(
                    packageName = pkg,
                    averageDailyUsageMs = (totalByPackage[pkg] ?: 0L) / DAYS_IN_WEEK,
                )
            }
        }
    }
