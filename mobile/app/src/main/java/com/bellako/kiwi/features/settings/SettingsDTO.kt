package com.bellako.kiwi.features.settings

import com.bellako.kiwi.types.Email

data class SettingsDTO(
    val email: String,
    val soundVolume: Int,
    val musicVolume: Int
) {
    fun toState(): SettingsState {
        return SettingsState(
            email = email,
            soundVolume = soundVolume,
            musicVolume = musicVolume
        )
    }

    fun toDomainObject(): Result<Settings> {
        return Email.of(email).map { validEmail ->
            Settings(
                email = validEmail,
                soundVolume = soundVolume,
                musicVolume = musicVolume
            )
        }
    }
}
