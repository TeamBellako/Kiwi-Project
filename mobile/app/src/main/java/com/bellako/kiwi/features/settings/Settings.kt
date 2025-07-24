package com.bellako.kiwi.features.settings

import com.bellako.kiwi.types.Email

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
