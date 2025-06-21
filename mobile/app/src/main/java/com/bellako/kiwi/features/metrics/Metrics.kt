package com.bellako.kiwi.features.metrics

import com.bellako.kiwi.features.users.Email
import java.time.LocalDate

data class Metrics (
    val email: Email,
    val date: LocalDate,
    val steps: PositiveOrZeroInteger,
    val screenTimeSeconds: PositiveOrZeroInteger
)