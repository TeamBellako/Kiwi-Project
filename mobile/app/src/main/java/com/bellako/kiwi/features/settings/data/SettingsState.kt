package com.bellako.kiwi.features.settings.data

import com.bellako.kiwi.features.users.data.Email

data class SettingsState(
    val email: String = "",
    val soundVolume: Float = 1f,
    val musicVolume: Float = 1f,
) {
    fun toDTO(): SettingsDTO =
        SettingsDTO(
            email = email,
            soundVolume = soundVolume,
            musicVolume = musicVolume,
        )

    fun toDomainObject(): Result<Settings> =
        Email.of(email).map { validEmail ->
            Settings(
                email = validEmail,
                soundVolume = soundVolume,
                musicVolume = musicVolume,
            )
        }
}
