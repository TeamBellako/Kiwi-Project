package com.bellako.kiwi.features.settings

import com.bellako.kiwi.types.Email


data class SettingsState(
    val email: String = "",
    val soundVolume: Float = 1f,
    val musicVolume: Float = 1f
) {
    fun toDTO(): SettingsDTO {
        return SettingsDTO(
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
