package com.bellako.kiwi.features.users.data

data class LoginDTO(
    val email: String,
    val password: String,
    val language: String? = null,
)
