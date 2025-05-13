package com.bellako.kiwi.userSettings.types

import com.bellako.kiwi.users.Email

data class UserSettingsDTO(
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
        return Email.of(email).map { validEmail ->
            UserSettings(
                email = validEmail,
                areNotificationsEnabled = areNotificationsEnabled,
                theme = Theme.valueOf(theme.name)
            )
        }
    }
}
