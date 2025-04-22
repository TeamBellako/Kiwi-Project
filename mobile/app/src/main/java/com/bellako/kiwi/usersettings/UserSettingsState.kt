package com.bellako.kiwi.usersettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class UserSettingsState(dto: UserSettingsDto) {
    var email by mutableStateOf(dto.email)
    var areNotificationsEnabled by mutableStateOf(dto.areNotificationsEnabled)
    var theme by mutableStateOf(dto.theme)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserSettingsState

        if (areNotificationsEnabled != other.areNotificationsEnabled) return false
        if (email != other.email) return false
        if (theme != other.theme) return false

        return true
    }

    override fun hashCode(): Int {
        var result = areNotificationsEnabled.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + theme.hashCode()
        return result
    }

    fun toDto() = UserSettingsDto(
        email = email,
        areNotificationsEnabled = areNotificationsEnabled,
        theme = theme
    )
}