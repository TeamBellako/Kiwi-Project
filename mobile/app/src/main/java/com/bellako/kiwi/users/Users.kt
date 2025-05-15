package com.bellako.kiwi.users

data class Users (
    val email: Email,
    val password: Password
){
    fun toDTO() : UsersDTO {
        return UsersDTO(
            email = email.value,
            password = password.value
        )
    }

    fun toState() : UsersState {
        return UsersState(
            email = email.value,
            password = password.value
        )
    }
}