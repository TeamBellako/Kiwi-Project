package com.bellako.kiwi.features.users.data

import java.time.LocalDate

data class UsersDomain(
    val email: Email,
    val password: Password,
    val registerDate: LocalDate,
)
