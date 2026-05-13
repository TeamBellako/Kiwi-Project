package com.bellako.kiwi.features.settings.model

import com.bellako.kiwi.features.settings.data.SettingsDTO

class SettingsRepository(
    private val api: ISettingsAPI,
) {
    suspend fun getSettings(): Result<SettingsDTO> = runCatching { api.getSettings() }

    suspend fun updateSettings(settings: SettingsDTO): Result<SettingsDTO> = runCatching { api.updateSettings(settings) }
}
