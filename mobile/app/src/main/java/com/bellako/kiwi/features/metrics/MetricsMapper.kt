package com.bellako.kiwi.features.metrics

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.users.Email
import java.time.LocalDate

object MetricsMapper {
    // DTO -> Domain
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: MetricsDTO): Result<Metrics> {
        val emailResult = Email.of(dto.email)
        val dateResult = runCatching { LocalDate.parse(dto.date) }
        val stepsResult = PositiveOrZeroInteger.of(dto.steps)
        val screenTimeResult = PositiveOrZeroInteger.of(dto.screenTimeSeconds)

        return combineResults(emailResult, dateResult, stepsResult, screenTimeResult) { email, date, steps, screenTime ->
            Metrics(email, date, steps, screenTime)
        }
    }

    // DTO -> State
    fun toState(dto: MetricsDTO): MetricsState = MetricsState(
        email = dto.email,
        date = dto.date,
        steps = dto.steps,
        screenTimeSeconds = dto.screenTimeSeconds
    )

    // Domain -> DTO
    fun toDTO(metrics: Metrics): MetricsDTO {
        return MetricsDTO(
            email = metrics.email.value,
            date = metrics.date.toString(),
            steps = metrics.steps.value,
            screenTimeSeconds = metrics.screenTimeSeconds.value
        )
    }

    // Domain -> State
    fun toState(metrics: Metrics): MetricsState {
        return MetricsState(
            email = metrics.email.value,
            date = metrics.date.toString(),
            steps = metrics.steps.value,
            screenTimeSeconds = metrics.screenTimeSeconds.value
        )
    }

    // State -> Domain
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(state: MetricsState): Result<Metrics> {
        val emailResult = Email.of(state.email)
        val dateResult = runCatching { LocalDate.parse(state.date) }
        val stepsResult = PositiveOrZeroInteger.of(state.steps)
        val screenTimeResult = PositiveOrZeroInteger.of(state.screenTimeSeconds)

        return combineResults(emailResult, dateResult, stepsResult, screenTimeResult) { email, date, steps, screenTime ->
            Metrics(email, date, steps, screenTime)
        }
    }

    // State -> DTO
    fun toDTO(state: MetricsState): MetricsDTO = MetricsDTO(
        email = state.email,
        date = state.date,
        steps = state.steps,
        screenTimeSeconds = state.screenTimeSeconds
    )

    private inline fun <A, B, C, D, R> combineResults(
        ra: Result<A>,
        rb: Result<B>,
        rc: Result<C>,
        rd: Result<D>,
        combine: (A, B, C, D) -> R
    ): Result<R> {
        return if (ra.isSuccess && rb.isSuccess && rc.isSuccess && rd.isSuccess) {
            Result.success(combine(ra.getOrThrow(), rb.getOrThrow(), rc.getOrThrow(), rd.getOrThrow()))
        } else {
            Result.failure(
                ra.exceptionOrNull() ?: rb.exceptionOrNull() ?: rc.exceptionOrNull() ?: rd.exceptionOrNull()
                ?: IllegalStateException("Unknown error in result combination")
            )
        }
    }
}
