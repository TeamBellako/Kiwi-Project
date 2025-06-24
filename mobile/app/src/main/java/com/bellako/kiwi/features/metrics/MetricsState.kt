package com.bellako.kiwi.features.metrics

data class MetricsState (
    val date: String,
    val steps: Int,
    val screenTimeSeconds: Int
) {
    fun isDefault(): Boolean {
        return steps <= 0 && screenTimeSeconds <= 0;
    }
}
