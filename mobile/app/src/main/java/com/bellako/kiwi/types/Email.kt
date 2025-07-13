package com.bellako.kiwi.types

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

        fun isValid(email: String): Boolean {
            return EMAIL_REGEX.matches(email)
        }

        fun of(email: String): Result<Email> {
            return if (isValid(email)) {
                Result.success(Email(email))
            } else {
                Result.failure(IllegalArgumentException("Invalid email format"))
            }
        }
    }
}