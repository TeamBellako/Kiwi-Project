package com.bellako.kiwi.features.users.data

// A single rule a password must satisfy, pairing the human-readable description
// shown to the user with the predicate that enforces it. Keeping both together
// means the explanation and the validation can never drift apart.
private data class PasswordRule(
    val description: String,
    val isSatisfiedBy: (String) -> Boolean,
)

@JvmInline
value class Password private constructor(
    val value: String,
) {
    companion object {
        const val MIN_LENGTH = 8

        private val RULES: List<PasswordRule> =
            listOf(
                PasswordRule("At least $MIN_LENGTH characters long") { it.length >= MIN_LENGTH },
            )

        // The rules the given password currently fails, used to tell the user
        // exactly why an entry is rejected. Empty when the password is valid.
        fun unmetRequirements(password: String): List<String> =
            RULES.filter { !it.isSatisfiedBy(password) }.map { it.description }

        fun isValid(password: String): Boolean = RULES.all { it.isSatisfiedBy(password) }

        fun of(password: String): Result<Password> =
            if (isValid(password)) {
                Result.success(Password(password))
            } else {
                Result.failure(
                    IllegalArgumentException(
                        "Password should be at least $MIN_LENGTH characters long",
                    ),
                )
            }
    }
}
