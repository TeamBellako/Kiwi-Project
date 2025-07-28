package com.bellako.kiwi.features.settings.model

import com.bellako.kiwi.features.settings.data.SettingsDTO
import com.bellako.kiwi.common.model.HealthApiService

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