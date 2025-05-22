package com.bellako.kiwi.userSettings.utils

import com.bellako.kiwi.userSettings.types.UserSettingsDTO

object UserSettingsTestFactory {

    fun validUserSettings(): UserSettingsDTO =
        UserSettingsDTO(
            email = "finn@thehuman.com",
            soundVolume = 67,
            musicVolume = 33
        )

    fun updateUserSettings(): UserSettingsDTO =
        UserSettingsDTO(
            email = "finn@thehuman.com",
            soundVolume = 33,
            musicVolume = 0
        )

    fun invalidUserSettings(): UserSettingsDTO =
        UserSettingsDTO(
            email = "bmolovesfootball.com",
            soundVolume = -10,
            musicVolume = 200
        )
}
