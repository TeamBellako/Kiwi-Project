package com.bellako.kiwi.userSettings.types

object UserSettingsFactory {

    fun fromDto(dto: UserSettingsDTO): Result<UserSettings> {
        return ValidatedEmail.of(dto.email).map { validEmail ->
            UserSettings(
                email = validEmail,
                areNotificationsEnabled = dto.areNotificationsEnabled,
                theme = UserSettings.Theme.valueOf(dto.theme.name)
            )
        }
    }

    fun fromState(state: UserSettingsState): Result<UserSettings> {
        return ValidatedEmail.of(state.email).map { validEmail ->
            UserSettings(
                email = validEmail,
                areNotificationsEnabled = state.areNotificationsEnabled,
                theme = state.theme
            )
        }
    }

    fun toDto(userSettings: UserSettings): UserSettingsDTO {
        return UserSettingsDTO(
            email = userSettings.email.value,
            areNotificationsEnabled = userSettings.areNotificationsEnabled,
            theme = UserSettings.Theme.valueOf(userSettings.theme.name)
        )
    }

    fun toState(userSettings: UserSettings): UserSettingsState {
        return UserSettingsState(
            email = userSettings.email.value,
            areNotificationsEnabled = userSettings.areNotificationsEnabled,
            theme = userSettings.theme
        )
    }
}
