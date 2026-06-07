package com.bellako.kiwi.features.users.data

sealed class PasswordError {
    data object TooShort : PasswordError()

    data object MissingDigit : PasswordError()

    data object MissingSymbol : PasswordError()

    data object MissingLetter : PasswordError()
}

fun PasswordError.toMessage(): String =
    when (this) {
        PasswordError.TooShort -> "Password should be at least 8 characters long."
        PasswordError.MissingDigit -> "Password must contain at least one number."
        PasswordError.MissingSymbol -> "Password must contain at least one symbol."
        PasswordError.MissingLetter -> "Password must contain at least one letter."
    }

@JvmInline
value class Password private constructor(
    val value: String,
) {
    companion object {
        private val MIN_LENGTH = 8
        private val DIGIT_REGEX = Regex(".*\\d.*")
        private val LETTER_REGEX = Regex(".*[A-Za-z].*")
        private val SYMBOL_REGEX = Regex(".*[^A-Za-z0-9].*")

        fun validate(password: String): List<PasswordError> {
            val errors = mutableListOf<PasswordError>()

            if (password.length < MIN_LENGTH) {
                errors.add(PasswordError.TooShort)
            }
            if (!DIGIT_REGEX.containsMatchIn(password)) {
                errors.add(PasswordError.MissingDigit)
            }
            if (!LETTER_REGEX.containsMatchIn(password)) {
                errors.add(PasswordError.MissingLetter)
            }
            if (!SYMBOL_REGEX.containsMatchIn(password)) {
                errors.add(PasswordError.MissingSymbol)
            }

            return errors
        }

        fun of(password: String): Result<Password> {
            val errors = validate(password)

            return if (errors.isEmpty()) {
                Result.success(Password(password))
            } else {
                Result.failure(
                    IllegalArgumentException(
                        errors.joinToString("\n") { "• ${it.toMessage()}" },
                    ),
                )
            }
        }
    }
}
