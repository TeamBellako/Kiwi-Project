package com.bellako.kiwi.settings

import com.bellako.kiwi.login.Email

data class Settings(
    val email: Email,
    val soundVolume: Int,
    val musicVolume: Int,
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
