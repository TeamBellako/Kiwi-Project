package com.bellako.kiwi.userSettings.types

data class UserSettingsDTO(
    val id: Int = 1, // TODO: Remove when JWT is implemented
    val email: String,
    val areNotificationsEnabled: Boolean,
    val theme: Theme

) {
    fun toState() : UserSettingsState {
        return UserSettingsState(
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
                theme = Theme.valueOf(theme.name)
            )
        }
    }
}
