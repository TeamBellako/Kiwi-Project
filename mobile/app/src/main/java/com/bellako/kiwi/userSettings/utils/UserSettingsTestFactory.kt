package com.bellako.kiwi.userSettings.utils

import com.bellako.kiwi.userSettings.types.Theme
import com.bellako.kiwi.userSettings.types.UserSettings
import com.bellako.kiwi.userSettings.types.UserSettingsDTO

object UserSettingsTestFactory {

    fun validUserSettings(): UserSettingsDTO =
        UserSettingsDTO(
            email = "finn@thehuman.com",
            areNotificationsEnabled = true,
            theme = Theme.DARK
        )


    fun updateUserSettings(): UserSettingsDTO =
        UserSettingsDTO(
            email = "jake@thedog.com",
            areNotificationsEnabled = false,
            theme = Theme.LIGHT
        )

    fun invalidUserSettings(): UserSettingsDTO =
        UserSettingsDTO(
            email = "bmolovesfootball.com", // invalid email
            areNotificationsEnabled = false,
            theme = Theme.LIGHT
        )
}
