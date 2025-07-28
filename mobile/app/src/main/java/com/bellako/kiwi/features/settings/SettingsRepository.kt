package com.bellako.kiwi.features.settings

import com.bellako.kiwi.services.network.HealthApiService

class SettingsRepository(
    private val api: ISettingsAPI,
    private val healthApi: HealthApiService
) {
    suspend fun getSettings(): Result<SettingsDTO> =
        runCatching { api.getSettings() }

    suspend fun updateSettings(settings: SettingsDTO): Result<Unit> =
        runCatching { api.updateSettings(settings) }

    suspend fun pingServer(): Result<Unit> =
        runCatching { healthApi.ping() }
}