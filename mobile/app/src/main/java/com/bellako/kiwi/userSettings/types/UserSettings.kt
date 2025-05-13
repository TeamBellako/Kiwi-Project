package com.bellako.kiwi.userSettings.types

import com.bellako.kiwi.users.Email

data class UserSettings(
    val email: Email,
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
