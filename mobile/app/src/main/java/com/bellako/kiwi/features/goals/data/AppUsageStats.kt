package com.bellako.kiwi.features.goals.data

data class AppUsageStats(
    val packageName: String,
    val averageDailyUsageMs: Long,
)

data class AppUsageResult(
    val goodAppsUsage: List<AppUsageStats>,
    val badAppsUsage: List<AppUsageStats>,
)
