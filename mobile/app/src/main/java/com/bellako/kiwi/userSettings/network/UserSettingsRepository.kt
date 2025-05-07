package com.bellako.kiwi.userSettings.network

import com.bellako.kiwi.network.createHealthApiService
import com.bellako.kiwi.userSettings.types.UserSettingsDTO

class UserSettingsRepository(private val api: IUserSettingsAPI) {

    suspend fun getUserSettings(): Result<UserSettingsDTO> =
        runCatching { api.getUserSettings() }

    suspend fun updateUserSettings(settings: UserSettingsDTO): Result<Unit> =
        runCatching { api.updateUserSettings(settings) }

    suspend fun pingServer(): Result<Unit> =
        runCatching { createHealthApiService().ping() }
}