package com.bellako.kiwi.features.users.data

data class UsersState(
    val email: String,
    val password: String,
) {
    fun toDTO(): UsersDTO =
        UsersDTO(
            email = email,
            password = password,
        )

    fun toDomainObject(): Result<Users> {
        val emailResult = Email.of(email)
        val passwordResult = Password.of(password)

        return emailResult.fold(
            onSuccess = { validEmail ->
                passwordResult.fold(
                    onSuccess = { validPassword ->
                        Result.success(Users(validEmail, validPassword))
                    },
                    onFailure = { err -> Result.failure(err) },
                )
            },
            onFailure = { err -> Result.failure(err) },
        )
    }
}
