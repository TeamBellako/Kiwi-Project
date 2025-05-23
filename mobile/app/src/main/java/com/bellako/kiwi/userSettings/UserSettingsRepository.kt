package com.bellako.kiwi.userSettings

import com.bellako.kiwi.network.HealthApiService

class UserSettingsRepository(
    private val api: IUserSettingsAPI,
    private val healthApi: HealthApiService
) {
    suspend fun getUserSettings(): Result<UserSettingsDTO> =
        runCatching { api.getUserSettings() }

    suspend fun updateUserSettings(settings: UserSettingsDTO): Result<Unit> =
        runCatching { api.updateUserSettings(settings) }

    suspend fun pingServer(): Result<Unit> =
        runCatching { healthApi.ping() }
}