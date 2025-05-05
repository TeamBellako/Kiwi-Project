package com.bellako.kiwi.userSettings.types

data class UserSettingsState(
    val email: String = "",
    val areNotificationsEnabled: Boolean = false,
    val theme: UserSettings.Theme = UserSettings.Theme.LIGHT
)