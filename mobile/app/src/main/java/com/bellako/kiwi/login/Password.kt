package com.bellako.kiwi.login

@JvmInline
value class Password private constructor(val value: String) {
    companion object {
        private val PASSWORD_REGEX = Regex(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
        )

        fun isValid(password: String): Boolean {
            return PASSWORD_REGEX.matches(password)
        }

        fun of(password: String): Result<Password> {
            return if (isValid(password)) {
                Result.success(Password(password))
            } else {
                Result.failure(IllegalArgumentException("Invalid password format"))
            }
        }
    }
}