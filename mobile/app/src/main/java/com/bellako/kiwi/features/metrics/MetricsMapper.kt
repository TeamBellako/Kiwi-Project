package com.bellako.kiwi.features.metrics

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.users.Email
import java.time.LocalDate

object MetricsMapper {
    // DTO -> Domain
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: MetricsDTO): Result<Metrics> {
        val dateResult = runCatching { LocalDate.parse(dto.date) }
        val stepsResult = PositiveOrZeroInteger.of(dto.steps)
        val screenTimeResult = PositiveOrZeroInteger.of(dto.screenTimeSeconds)

        return combineResults(dateResult, stepsResult, screenTimeResult) { date, steps, screenTime ->
            Metrics(date, steps, screenTime)
        }
    }

    // DTO -> State
    fun toState(dto: MetricsDTO): MetricsState = MetricsState(
        date = dto.date,
        steps = dto.steps,
        screenTimeSeconds = dto.screenTimeSeconds
    )

    // Domain -> DTO
    fun toDTO(metrics: Metrics): MetricsDTO {
        return MetricsDTO(
            date = metrics.date.toString(),
            steps = metrics.steps.value,
            screenTimeSeconds = metrics.screenTimeSeconds.value
        )
    }

    // Domain -> State
    fun toState(metrics: Metrics): MetricsState {
        return MetricsState(
            date = metrics.date.toString(),
            steps = metrics.steps.value,
            screenTimeSeconds = metrics.screenTimeSeconds.value
        )
    }

    // State -> Domain
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(state: MetricsState): Result<Metrics> {
        val dateResult = runCatching { LocalDate.parse(state.date) }
        val stepsResult = PositiveOrZeroInteger.of(state.steps)
        val screenTimeResult = PositiveOrZeroInteger.of(state.screenTimeSeconds)

        return combineResults(dateResult, stepsResult, screenTimeResult) { date, steps, screenTime ->
            Metrics(date, steps, screenTime)
        }
    }

    // State -> DTO
    fun toDTO(state: MetricsState): MetricsDTO = MetricsDTO(
        date = state.date,
        steps = state.steps,
        screenTimeSeconds = state.screenTimeSeconds
    )

    private inline fun <A, B, C, R> combineResults(
        ra: Result<A>,
        rb: Result<B>,
        rc: Result<C>,
        combine: (A, B, C) -> R
    ): Result<R> {
        return if (ra.isSuccess && rb.isSuccess && rc.isSuccess) {
            Result.success(combine(ra.getOrThrow(), rb.getOrThrow(), rc.getOrThrow()))
        } else {
            Result.failure(
                ra.exceptionOrNull() ?: rb.exceptionOrNull() ?: rc.exceptionOrNull()
                ?: IllegalStateException("Unknown error in result combination")
            )
        }
    }
}
