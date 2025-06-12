package com.bellako.kiwi.settings

object SettingsTestFactory {

    fun validSettings(): SettingsDTO =
        SettingsDTO(
            email = "finn@thehuman.com",
            soundVolume = 67,
            musicVolume = 33
        )

    fun updateSettings(): SettingsDTO =
        SettingsDTO(
            email = "finn@thehuman.com",
            soundVolume = 33,
            musicVolume = 0
        )

    fun invalidSettings(): SettingsDTO =
        SettingsDTO(
            email = "bmolovesfootball.com",
            soundVolume = -10,
            musicVolume = 200
        )
}
