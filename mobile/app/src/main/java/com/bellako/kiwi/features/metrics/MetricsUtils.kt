package com.bellako.kiwi.features.metrics

import java.time.LocalDate

object MetricsUtils {
    fun parseScreenTimeSeconds(screenTimeSeconds: Int): String {
        val hours = screenTimeSeconds / 3600
        val minutes = (screenTimeSeconds % 3600) / 60

        return buildString {
            if (hours >= 0) append("${hours}h ")
            if (minutes >= 0) append("${minutes}min")
        }.trim()
    }

    fun getDayOfWeekNumber(date: LocalDate): Int {
        val dayOfWeek = date.dayOfWeek
        return (dayOfWeek.ordinal + 1) % 7
    }
}