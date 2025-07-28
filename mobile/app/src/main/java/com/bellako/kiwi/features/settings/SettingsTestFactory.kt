package com.bellako.kiwi.features.settings

object SettingsTestFactory {

    fun validSettings(): SettingsDTO =
        SettingsDTO(
            email = "finn@thehuman.com",
            soundVolume = 0.67f,
            musicVolume = 0.33f
        )

    fun updateSettings(): SettingsDTO =
        SettingsDTO(
            email = "finn@thehuman.com",
            soundVolume = 0.33f,
            musicVolume = 0f
        )

    fun invalidSettings(): SettingsDTO =
        SettingsDTO(
            email = "bmolovesfootball.com",
            soundVolume = -0.1f,
            musicVolume = 0.2f
        )
}
