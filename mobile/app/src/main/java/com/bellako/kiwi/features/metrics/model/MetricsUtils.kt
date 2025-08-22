package com.bellako.kiwi.features.metrics.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@Suppress("MagicNumber")
object MetricsUtils {
    fun parseTimeSeconds(screenTimeSeconds: Int): String {
        val hours = screenTimeSeconds / 3600
        val minutes = (screenTimeSeconds % 3600) / 60

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours <= 0) append("${minutes}min")
        }.trim()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfWeekNumber(date: LocalDate): Int {
        val dayOfWeek = date.dayOfWeek
        return (dayOfWeek.ordinal + 1) % 7
    }
}
