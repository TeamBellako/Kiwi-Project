package com.bellako.kiwi.features.metrics

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
object MetricsFactory {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun generateRandomValidMetricDTO(): MetricsDTO {
        return MetricsDTO(
            email = "finn@thehuman.com",
            date = getRandomDate(),
            steps = getRandomSteps(),
            screenTimeSeconds = getRandomScreenTimeSeconds()
        )
    }

    fun generateRandomInvalidMetricDTO(): MetricsDTO {
        return MetricsDTO(
            email = "finn@thehuman.com",
            date = getRandomDate(),
            steps = -getRandomSteps(),
            screenTimeSeconds = -getRandomScreenTimeSeconds()
        )
    }

    fun generateRandomMetricsSet(size: Int, valid: Boolean): Set<MetricsDTO> {
        return (1..size).map {
            if (valid) generateRandomValidMetricDTO() else generateRandomInvalidMetricDTO()
        }.toSet()
    }

    private fun getRandomDate(): String {
        val year = Random.nextInt(2025, 2027)
        val month = Random.nextInt(1, 13)
        val day = Random.nextInt(1, 29)

        return LocalDate.of(year, month, day).format(formatter)
    }

    private fun getRandomSteps(): Int {
        return Random.nextInt(1, 10001)
    }

    private fun getRandomScreenTimeSeconds(): Int {
        return Random.nextInt(60, 4 * 60 * 60 + 1)
    }
}