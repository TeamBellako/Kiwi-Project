package com.bellako.kiwi.usersettings

data class UserSettingsDto(
    val email: String = "",
    val areNotificationsEnabled: Boolean = false,
    val theme: Theme = Theme.LIGHT
) {
    enum class Theme {
        LIGHT,
        DARK
    }
}

fun UserSettingsDto.toState() = UserSettingsState(this)