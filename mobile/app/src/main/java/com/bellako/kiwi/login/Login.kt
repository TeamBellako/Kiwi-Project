package com.bellako.kiwi.login

data class Login (
    val email: Email,
    val password: Password
){
    fun toDTO() : LoginDTO {
        return LoginDTO(
            email = email.value,
            password = password.value
        )
    }

    fun toState() : LoginState {
        return LoginState(
            email = email.value,
            password = password.value
        )
    }
}