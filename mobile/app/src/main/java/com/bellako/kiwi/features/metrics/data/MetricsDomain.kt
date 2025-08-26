package com.bellako.kiwi.features.metrics.data

import java.time.LocalDate

data class MetricsDomain(
    val date: LocalDate,
    val maxGoodTimeSeconds: Int,
    val currentGoodTimeSeconds: Int,
    val maxBadTimeSeconds: Int,
    val currentBadTimeSeconds: Int,
)
