package com.bellako.kiwi.features.metrics.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.metrics.data.Metrics
import com.bellako.kiwi.features.metrics.data.MetricsDTO
import com.bellako.kiwi.features.metrics.data.MetricsState
import java.time.LocalDate

object MetricsMapper {
    // DTO -> Domain
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: MetricsDTO): Metrics =
        Metrics(
            date = LocalDate.parse(dto.date),
            maxGoodTimeSeconds = dto.maxGoodTimeSeconds,
            currentGoodTimeSeconds = dto.currentGoodTimeSeconds,
            maxBadTimeSeconds = dto.maxBadTimeSeconds,
            currentBadTimeSeconds = dto.currentBadTimeSeconds,
        )

    // DTO -> State
    fun toState(dto: MetricsDTO): MetricsState =
        MetricsState(
            date = dto.date,
            maxGoodTimeSeconds = dto.maxGoodTimeSeconds,
            currentGoodTimeSeconds = dto.currentGoodTimeSeconds,
            maxBadTimeSeconds = dto.maxBadTimeSeconds,
            currentBadTimeSeconds = dto.currentBadTimeSeconds,
        )

    // Domain -> DTO
    fun toDTO(metrics: Metrics): MetricsDTO =
        MetricsDTO(
            date = metrics.date.toString(),
            maxGoodTimeSeconds = metrics.maxGoodTimeSeconds,
            currentGoodTimeSeconds = metrics.currentGoodTimeSeconds,
            maxBadTimeSeconds = metrics.maxBadTimeSeconds,
            currentBadTimeSeconds = metrics.currentBadTimeSeconds,
        )

    // Domain -> State
    fun toState(metrics: Metrics): MetricsState =
        MetricsState(
            date = metrics.date.toString(),
            maxGoodTimeSeconds = metrics.maxGoodTimeSeconds,
            currentGoodTimeSeconds = metrics.currentGoodTimeSeconds,
            maxBadTimeSeconds = metrics.maxBadTimeSeconds,
            currentBadTimeSeconds = metrics.currentBadTimeSeconds,
        )

    // State -> Domain
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(state: MetricsState): Metrics =
        Metrics(
            date = LocalDate.parse(state.date),
            maxGoodTimeSeconds = state.maxGoodTimeSeconds,
            currentGoodTimeSeconds = state.currentGoodTimeSeconds,
            maxBadTimeSeconds = state.maxBadTimeSeconds,
            currentBadTimeSeconds = state.currentBadTimeSeconds,
        )

    // State -> DTO
    fun toDTO(state: MetricsState): MetricsDTO =
        MetricsDTO(
            date = state.date,
            maxGoodTimeSeconds = state.maxGoodTimeSeconds,
            currentGoodTimeSeconds = state.currentGoodTimeSeconds,
            maxBadTimeSeconds = state.maxBadTimeSeconds,
            currentBadTimeSeconds = state.currentBadTimeSeconds,
        )
}
