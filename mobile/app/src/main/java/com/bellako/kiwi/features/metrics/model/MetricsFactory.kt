package com.bellako.kiwi.features.metrics.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.SECONDS_IN_HOUR
import com.bellako.kiwi.features.metrics.data.MetricsDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Suppress("MagicNumber")
@RequiresApi(Build.VERSION_CODES.O)
object MetricsFactory {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun generateRandomValidMetricDTO(): MetricsDTO =
        MetricsDTO(
            date = getRandomDate(),
            maxGoodTimeSeconds = getRandomTimeSeconds(5, 6),
            currentGoodTimeSeconds = getRandomTimeSeconds(1, 2),
            maxBadTimeSeconds = getRandomTimeSeconds(5, 6),
            currentBadTimeSeconds = getRandomTimeSeconds(3, 4),
        )

    fun generateRandomInvalidMetricDTO(): MetricsDTO =
        MetricsDTO(
            date = getRandomDate(),
            maxGoodTimeSeconds = -getRandomTimeSeconds(5, 6),
            currentGoodTimeSeconds = -getRandomTimeSeconds(1, 2),
            maxBadTimeSeconds = -getRandomTimeSeconds(5, 6),
            currentBadTimeSeconds = -getRandomTimeSeconds(3, 4),
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

    private fun getRandomTimeSeconds(
        fromHours: Int,
        untilHours: Int,
    ): Int = Random.nextInt(fromHours * SECONDS_IN_HOUR, untilHours * SECONDS_IN_HOUR)
}
