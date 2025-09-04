package com.bellako.kiwi.common.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

const val SECONDS_IN_MINUTE = 60
const val MINUTES_IN_HOUR = 60
const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR
const val DAYS_IN_WEEK = 7

object DateUtils {
    fun parseTimeSeconds(screenTimeSeconds: Int): String {
        val hours = screenTimeSeconds / SECONDS_IN_HOUR
        val minutes = (screenTimeSeconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours <= 0) append("${minutes}min")
        }.trim()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfWeekNumber(date: LocalDate): Int {
        val dayOfWeek = date.dayOfWeek
        return (dayOfWeek.ordinal + 1) % DAYS_IN_WEEK
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(date: YearMonth): String = date.format(DateTimeFormatter.ofPattern("MM-yyyy"))
}
