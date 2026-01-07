package com.bellako.kiwi.features.users.data

@JvmInline
value class Password private constructor(
    val value: String,
) {
    companion object {
        private val PASSWORD_REGEX_LENGTH = Regex("^.{8,}$")

        fun isValid(password: String): Boolean = PASSWORD_REGEX_LENGTH.matches(password)

        fun of(password: String): Result<Password> =
            if (isValid(password)) {
                Result.success(Password(password))
            } else {
                Result.failure(
                    IllegalArgumentException(
                        if (!PASSWORD_REGEX_LENGTH.matches(password)) {
                            "Password should be at least 8 characters long"
                        } else {
                            "Invalid password format"
                        },
                    ),
                )
            }
    }
}
