package com.bellako.kiwi.features.settings.data

import com.bellako.kiwi.features.users.data.Email

data class Settings(
    val email: Email,
    val soundVolume: Float,
    val musicVolume: Float,
) {
    fun toDTO(): SettingsDTO =
        SettingsDTO(
            email = email.value,
            soundVolume = soundVolume,
            musicVolume = musicVolume,
        )

    fun toState(): SettingsState =
        SettingsState(
            email = email.value,
            soundVolume = soundVolume,
            musicVolume = musicVolume,
        )
}
