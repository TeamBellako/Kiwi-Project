package com.bellako.kiwi.features.metrics.data

data class MetricsState(
    val date: String,
    val maxGoodTimeSeconds: Int,
    val currentGoodTimeSeconds: Int,
    val maxBadTimeSeconds: Int,
    val currentBadTimeSeconds: Int,
) {
    fun getAppUsageProgress(): Float {
        val goodTimeProgress =
            if (maxGoodTimeSeconds > 0) {
                (currentGoodTimeSeconds.toFloat() / maxGoodTimeSeconds.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

        val badTimeProgress =
            if (maxBadTimeSeconds > 0) {
                (1f - currentBadTimeSeconds.toFloat() / maxBadTimeSeconds.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

        return (goodTimeProgress + badTimeProgress) / 2f
    }
}