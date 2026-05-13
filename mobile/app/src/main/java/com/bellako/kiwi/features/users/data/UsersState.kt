package com.bellako.kiwi.features.users.data

data class UsersState(
    val email: String,
    val password: String,
    val registerDate: String,
    val currentPoints: Int = 0,
    val totalPoints: Int = 0,
)
