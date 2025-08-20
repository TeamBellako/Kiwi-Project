package com.bellako.kiwi.features.metrics.data
import com.bellako.kiwi.common.data.PositiveOrZeroInteger
import java.time.LocalDate

data class Metrics(
    val date: LocalDate,
    val steps: PositiveOrZeroInteger,
    val screenTimeSeconds: PositiveOrZeroInteger,
)
