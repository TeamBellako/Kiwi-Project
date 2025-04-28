package com.bellako.kiwi.usersettings

import com.bellako.kiwi.network.createHealthApiService

class UserSettingsRepository(private val api: IUserSettingsApi) {

    suspend fun getUserSettings(): Result<UserSettingsDto> =
        runCatching { api.getUserSettings() }

    suspend fun updateUserSettings(settings: UserSettingsDto): Result<Unit> =
        runCatching { api.updateUserSettings(settings) }

    suspend fun pingServer(): Result<Unit> =
        runCatching { createHealthApiService().ping() }
}