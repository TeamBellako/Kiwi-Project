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
            email = "finn@thehuman.com",
            areNotificationsEnabled = false,
            theme = Theme.LIGHT
        )

    fun invalidUserSettings(): UserSettingsDTO =
        UserSettingsDTO(
            email = "bmolovesfootball.com",
            areNotificationsEnabled = false,
            theme = Theme.LIGHT
        )
}
