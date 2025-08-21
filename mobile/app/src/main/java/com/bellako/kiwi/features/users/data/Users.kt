package com.bellako.kiwi.features.users.data

data class Users(
    val email: Email,
    val password: Password,
) {
    fun toDTO(): UsersDTO =
        UsersDTO(
            email = email.value,
            password = password.value,
        )

    fun toState(): UsersState =
        UsersState(
            email = email.value,
            password = password.value,
        )
}
