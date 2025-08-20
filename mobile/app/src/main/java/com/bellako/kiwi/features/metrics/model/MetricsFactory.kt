package com.bellako.kiwi.features.metrics.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.metrics.data.MetricsDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
object MetricsFactory {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun generateRandomValidMetricDTO(): MetricsDTO =
        MetricsDTO(
            date = getRandomDate(),
            steps = getRandomSteps(),
            screenTimeSeconds = getRandomScreenTimeSeconds(),
        )

    fun generateRandomInvalidMetricDTO(): MetricsDTO =
        MetricsDTO(
            date = getRandomDate(),
            steps = -getRandomSteps(),
            screenTimeSeconds = -getRandomScreenTimeSeconds(),
        )

    fun generateRandomMetricsSet(
        size: Int,
        valid: Boolean,
    ): Set<MetricsDTO> =
        (1..size)
            .map {
                if (valid) generateRandomValidMetricDTO() else generateRandomInvalidMetricDTO()
            }.toSet()

    private fun getRandomDate(): String {
        val year = Random.nextInt(2025, 2027)
        val month = Random.nextInt(1, 13)
        val day = Random.nextInt(1, 29)

        return LocalDate.of(year, month, day).format(formatter)
    }

    private fun getRandomSteps(): Int = Random.nextInt(1, 10001)

    private fun getRandomScreenTimeSeconds(): Int {
        return Random.nextInt(60, 10 * 60 * 60 + 1) // 60 to 36,000 seconds
    }
}
