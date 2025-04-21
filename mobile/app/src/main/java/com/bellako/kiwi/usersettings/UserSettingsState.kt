package com.bellako.kiwi.usersettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class UserSettingsState(dto: UserSettingsDto) {
    var email by mutableStateOf(dto.email)
    var areNotificationsEnabled by mutableStateOf(dto.areNotificationsEnabled)
    var theme by mutableStateOf(dto.theme)
}

fun UserSettingsState.toDto() = UserSettingsDto(
    email = email,
    areNotificationsEnabled = areNotificationsEnabled,
    theme = theme
)