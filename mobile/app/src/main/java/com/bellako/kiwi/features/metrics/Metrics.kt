package com.bellako.kiwi.features.metrics

import java.time.LocalDate

data class Metrics (
    val date: LocalDate,
    val steps: PositiveOrZeroInteger,
    val screenTimeSeconds: PositiveOrZeroInteger
)