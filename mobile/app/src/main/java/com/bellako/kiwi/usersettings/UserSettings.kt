package com.bellako.kiwi.usersettings

data class UserSettings(
    val email: String,
    val areNotificationsEnabled: Boolean,
    val theme: Theme
) {
    enum class Theme {
        LIGHT,
        DARK
    }
}