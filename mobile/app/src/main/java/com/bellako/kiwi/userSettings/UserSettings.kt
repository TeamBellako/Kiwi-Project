package com.bellako.kiwi.userSettings

import com.bellako.kiwi.users.Email

data class UserSettings(
    val email: Email,
    val soundVolume: Int,
    val musicVolume: Int,
) {
    fun toDTO(): UserSettingsDTO {
        return UserSettingsDTO(
            email = email.value,
            soundVolume = soundVolume,
            musicVolume = musicVolume
        )
    }

    fun toState(): UserSettingsState {
        return UserSettingsState(
            email = email.value,
            soundVolume = soundVolume,
            musicVolume = musicVolume
        )
    }
}
