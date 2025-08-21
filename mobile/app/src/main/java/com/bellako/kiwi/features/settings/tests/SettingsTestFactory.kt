package com.bellako.kiwi.features.settings.tests

import com.bellako.kiwi.features.settings.data.SettingsDTO
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO

object SettingsTestFactory {
    fun validSettings(): SettingsDTO =
        SettingsDTO(
            email = validUsersDTO().email,
            soundVolume = 0.67f,
            musicVolume = 0.33f,
        )

    fun updateSettings(): SettingsDTO =
        SettingsDTO(
            email = validUsersDTO().email,
            soundVolume = 0.33f,
            musicVolume = 0f,
        )

    fun invalidSettings(): SettingsDTO =
        SettingsDTO(
            email = "bmolovesfootball.com",
            soundVolume = -0.1f,
            musicVolume = 0.2f,
        )
}
