package com.bellako.kiwi.userSettings

import com.bellako.kiwi.users.Email

data class UserSettingsState(
    val email: String = "",
    val soundVolume: Int = 50,
    val musicVolume: Int = 50
) {
    fun toDTO(): UserSettingsDTO {
        return UserSettingsDTO(
            email = email,
            soundVolume = soundVolume,
            musicVolume = musicVolume
        )
    }

    fun toDomainObject(): Result<UserSettings> {
        return Email.of(email).map { validEmail ->
            UserSettings(
                email = validEmail,
                soundVolume = soundVolume,
                musicVolume = musicVolume
            )
        }
    }
}
