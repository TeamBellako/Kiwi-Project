package com.bellako.kiwi.usersettings

data class UserSettingsDto(
    val email: String,
    val areNotificationsEnabled: Boolean,
    val theme: Theme
) {
    enum class Theme {
        LIGHT,
        DARK
    }
}