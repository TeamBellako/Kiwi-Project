package com.bellako.kiwi.login

data class LoginDTO (
    val email : String,
    val password : String
) {
    fun toState() : LoginState {
        return LoginState(
            email = email,
            password = password
        )
    }

    fun toDomainObject(): Result<Login> {
        val emailResult = Email.of(email)
        val passwordResult = Password.of(password)

        return emailResult.fold(
            onSuccess = { validEmail ->
                passwordResult.fold(
                    onSuccess = { validPassword ->
                        Result.success(Login(validEmail, validPassword))
                    },
                    onFailure = { err -> Result.failure(err) }
                )
            },
            onFailure = { err -> Result.failure(err) }
        )
    }
}