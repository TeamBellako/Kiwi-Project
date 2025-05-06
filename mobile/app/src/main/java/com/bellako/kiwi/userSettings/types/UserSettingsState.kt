package com.bellako.kiwi.userSettings.types

data class UserSettingsState(
    val email: String = "",
    val areNotificationsEnabled: Boolean = false,
    val theme: Theme = Theme.LIGHT
) {
    fun toDto() : UserSettingsDTO {
        return UserSettingsDTO(
            email = email,
            areNotificationsEnabled = areNotificationsEnabled,
            theme = theme,
        )
    }

    fun toDomainObject(): Result<UserSettings> {
        return ValidatedEmail.of(email).map { validEmail ->
            UserSettings(
                email = validEmail,
                areNotificationsEnabled = areNotificationsEnabled,
                theme = theme
            )
        }
    }
}