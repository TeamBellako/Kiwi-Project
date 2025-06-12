package com.bellako.kiwi.login

data class LoginState (
    val email: String,
    val password: String
){
    fun toDTO() : LoginDTO {
        return LoginDTO(
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