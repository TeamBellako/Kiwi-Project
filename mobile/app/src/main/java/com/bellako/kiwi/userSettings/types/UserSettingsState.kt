package com.bellako.kiwi.userSettings.types

import com.bellako.kiwi.users.Email

data class UserSettingsState(
    val email: String = "",
    val areNotificationsEnabled: Boolean = false,
    val theme: Theme = Theme.LIGHT
) {
    fun toDTO() : UserSettingsDTO {
        return UserSettingsDTO(
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
                theme = theme
            )
        }
    }
}