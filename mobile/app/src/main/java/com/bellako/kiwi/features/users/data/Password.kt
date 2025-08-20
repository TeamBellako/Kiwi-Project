package com.bellako.kiwi.features.users.data

@JvmInline
value class Password private constructor(
    val value: String,
) {
    companion object {
        private val PASSWORD_REGEX_FULL = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@\$!%*?&]{8,}\$")
        private val PASSWORD_REGEX_LENGTH = Regex("^.{8,}$")
        private val PASSWORD_REGEX_UPPER = Regex(".*[A-Z].*")
        private val PASSWORD_REGEX_LOWER = Regex(".*[a-z].*")
        private val PASSWORD_REGEX_NUMBER = Regex(".*\\d.*")

        fun isValid(password: String): Boolean = PASSWORD_REGEX_FULL.matches(password)

        fun of(password: String): Result<Password> =
            if (isValid(password)) {
                Result.success(Password(password))
            } else {
                Result.failure(
                    IllegalArgumentException(
                        if (!PASSWORD_REGEX_LENGTH.matches(password)) {
                            "Password should be at least 8 characters long"
                        } else if (!PASSWORD_REGEX_UPPER.matches(password)) {
                            "Password should include at least one uppercase letter"
                        } else if (!PASSWORD_REGEX_LOWER.matches(password)) {
                            "Password should include at least one lowercase letter"
                        } else if (!PASSWORD_REGEX_NUMBER.matches(password)) {
                            "Password should contain at least one number"
                        } else {
                            "Invalid password"
                        },
                    ),
                )
            }
    }
}
