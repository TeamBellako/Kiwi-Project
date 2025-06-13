package com.bellako.kiwi.features.settings

import com.bellako.kiwi.features.users.Email


data class SettingsState(
    val email: String = "",
    val soundVolume: Int = 50,
    val musicVolume: Int = 50
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
