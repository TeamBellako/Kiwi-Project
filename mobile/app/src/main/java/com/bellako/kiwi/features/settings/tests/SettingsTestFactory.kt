package com.bellako.kiwi.features.settings.tests

import com.bellako.kiwi.features.settings.data.SettingsDTO

object SettingsTestFactory {
    fun validSettings(): SettingsDTO =
        SettingsDTO(
            soundVolume = 0.67f,
            musicVolume = 0.33f,
        )

    fun updatedSettings(): SettingsDTO =
        SettingsDTO(
            soundVolume = 0.33f,
            musicVolume = 0f,
        )

    fun invalidSettings(): SettingsDTO =
        SettingsDTO(
            soundVolume = -0.1f,
            musicVolume = 0.2f,
        )
}
