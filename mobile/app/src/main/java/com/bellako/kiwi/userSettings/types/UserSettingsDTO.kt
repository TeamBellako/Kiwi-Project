package com.bellako.kiwi.userSettings.types

data class UserSettingsDTO(
    val id: Int = 1, // TODO: Remove when JWT is implemented
    val email: String,
    val areNotificationsEnabled: Boolean,
    val theme: UserSettings.Theme
)