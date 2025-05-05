package com.bellako.kiwi.userSettings.utils

import com.bellako.kiwi.userSettings.types.UserSettings
import com.bellako.kiwi.userSettings.types.UserSettingsDTO
import com.bellako.kiwi.userSettings.types.UserSettingsFactory

object UserSettingsTestFactory {

    fun validUserSettings(): UserSettingsDTO =
        UserSettingsFactory.toDto(
            UserSettingsFactory.fromDto(
                UserSettingsDTO(
                    email = "finn@thehuman.com",
                    areNotificationsEnabled = true,
                    theme = UserSettings.Theme.DARK
                )
            ).getOrThrow()
        )

    fun updateUserSettings(): UserSettingsDTO =
        UserSettingsFactory.toDto(
            UserSettingsFactory.fromDto(
                UserSettingsDTO(
                    email = "jake@thedog.com",
                    areNotificationsEnabled = false,
                    theme = UserSettings.Theme.LIGHT
                )
            ).getOrThrow()
        )

    fun invalidUserSettings(): UserSettingsDTO =
        // This one must skip the factory to simulate failure intentionally
        UserSettingsDTO(
            email = "bmolovesfootball.com", // invalid email
            areNotificationsEnabled = false,
            theme = UserSettings.Theme.LIGHT
        )
}
