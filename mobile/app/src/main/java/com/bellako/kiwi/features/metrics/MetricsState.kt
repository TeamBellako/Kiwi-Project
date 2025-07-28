package com.bellako.kiwi.features.metrics

data class MetricsState (
    val date: String,
    val steps: Int = 0,
    val screenTimeSeconds: Int = 0
)
