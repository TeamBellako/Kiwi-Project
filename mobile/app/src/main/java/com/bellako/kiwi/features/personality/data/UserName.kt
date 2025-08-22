package com.bellako.kiwi.features.personality.data

@JvmInline
value class UserName private constructor(
    val value: String,
) {
    companion object {
        private val NAME_REGEX_FULL = Regex("^[a-zA-Z0-9]+$")
        private val NAME_REGEX_LENGTH = Regex("^.+$")

        fun isValid(name: String): Boolean = NAME_REGEX_FULL.matches(name)

        fun of(name: String): Result<UserName> =
            if (isValid(name)) {
                Result.success(UserName(name))
            } else {
                Result.failure(
                    IllegalArgumentException(
                        if (!NAME_REGEX_LENGTH.matches(name)) {
                            "Name should be at least 3 characters long"
                        } else {
                            "Invalid name"
                        },
                    ),
                )
            }
    }
}
