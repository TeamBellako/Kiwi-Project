package com.bellako.kiwi.usersettings

class UserSettingsRepository(private val api: IUserSettingsApi) {
    suspend fun getUserSettings(): Result<UserSettingsDto> =
        runCatching { api.getUserSettings() }

    suspend fun updateUserSettings(settings: UserSettingsDto): Result<Unit> =
        runCatching { api.updateUserSettings(settings) }
}