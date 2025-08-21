package com.bellako.kiwi.features.settings.data

import com.bellako.kiwi.features.users.data.Email

data class SettingsDTO(
    val email: String,
    val soundVolume: Float,
    val musicVolume: Float,
) {
    fun toState(): SettingsState =
        SettingsState(
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
