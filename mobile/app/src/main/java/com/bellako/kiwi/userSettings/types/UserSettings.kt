package com.bellako.kiwi.userSettings.types

@JvmInline
value class ValidatedEmail private constructor(val value: String) {
    companion object {
        fun isValid(email: String): Boolean {
            return email.contains("@") && email.contains(".")
        }

        fun of(email: String): Result<ValidatedEmail> {
            return if (isValid(email)) {
                Result.success(ValidatedEmail(email))
            } else {
                Result.failure(IllegalArgumentException("Invalid email format"))
            }
        }
    }
}

data class UserSettings(
    val email: ValidatedEmail,
    val areNotificationsEnabled: Boolean,
    val theme: Theme
) {
    fun toDTO(): UserSettingsDTO {
        return UserSettingsDTO(
            email = email.value,
            areNotificationsEnabled = areNotificationsEnabled,
            theme = Theme.valueOf(theme.name)
        )
    }

    fun toState(): UserSettingsState {
        return UserSettingsState(
            email = email.value,
            areNotificationsEnabled = areNotificationsEnabled,
            theme = theme
        )
    }
}
