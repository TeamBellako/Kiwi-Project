package com.bellako.kiwi.userSettings

import com.bellako.kiwi.users.Email

data class UserSettingsDTO(
    val email: String,
    val soundVolume: Int,
    val musicVolume: Int
) {
    fun toState(): UserSettingsState {
        return UserSettingsState(
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
