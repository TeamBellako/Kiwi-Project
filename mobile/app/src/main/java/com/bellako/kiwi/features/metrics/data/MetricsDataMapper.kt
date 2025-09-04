package com.bellako.kiwi.features.metrics.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.DateUtils.stringToDate

object MetricsDataMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: MetricsDTO): MetricsDomain =
        MetricsDomain(
            date = stringToDate(dto.date),
            maxGoodTimeSeconds = dto.maxGoodTimeSeconds,
            currentGoodTimeSeconds = dto.currentGoodTimeSeconds,
            maxBadTimeSeconds = dto.maxBadTimeSeconds,
            currentBadTimeSeconds = dto.currentBadTimeSeconds,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(state: MetricsState): MetricsDomain =
        MetricsDomain(
            date = stringToDate(state.date),
            maxGoodTimeSeconds = state.maxGoodTimeSeconds,
            currentGoodTimeSeconds = state.currentGoodTimeSeconds,
            maxBadTimeSeconds = state.maxBadTimeSeconds,
            currentBadTimeSeconds = state.currentBadTimeSeconds,
        )

    fun toState(domain: MetricsDomain): MetricsState =
        MetricsState(
            date = domain.date.toString(),
            maxGoodTimeSeconds = domain.maxGoodTimeSeconds,
            currentGoodTimeSeconds = domain.currentGoodTimeSeconds,
            maxBadTimeSeconds = domain.maxBadTimeSeconds,
            currentBadTimeSeconds = domain.currentBadTimeSeconds,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toState(dto: MetricsDTO): MetricsState = toState(toDomain(dto))

    fun toDTO(domain: MetricsDomain): MetricsDTO =
        MetricsDTO(
            date = domain.date.toString(),
            maxGoodTimeSeconds = domain.maxGoodTimeSeconds,
            currentGoodTimeSeconds = domain.currentGoodTimeSeconds,
            maxBadTimeSeconds = domain.maxBadTimeSeconds,
            currentBadTimeSeconds = domain.currentBadTimeSeconds,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDTO(state: MetricsState): MetricsDTO = toDTO(toDomain(state))
}
