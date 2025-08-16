package com.bellako.kiwi.features.settings.data

import com.bellako.kiwi.features.users.data.Email

data class Settings(
    val email: Email,
    val soundVolume: Float,
    val musicVolume: Float,
) {
    fun toDTO(): SettingsDTO {
        return SettingsDTO(
            email = email.value,
            soundVolume = soundVolume,
            musicVolume = musicVolume
        )
    }

    fun toState(): SettingsState {
        return SettingsState(
            email = email.value,
            soundVolume = soundVolume,
            musicVolume = musicVolume
        )
    }
}
