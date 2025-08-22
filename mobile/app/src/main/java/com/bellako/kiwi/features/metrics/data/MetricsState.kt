package com.bellako.kiwi.features.metrics.data

data class MetricsState(
    val date: String,
    val maxGoodTimeSeconds: Int,
    val currentGoodTimeSeconds: Int,
    val maxBadTimeSeconds: Int,
    val currentBadTimeSeconds: Int,
)
