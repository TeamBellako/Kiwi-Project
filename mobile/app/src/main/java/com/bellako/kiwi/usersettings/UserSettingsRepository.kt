package com.bellako.kiwi.usersettings

class UserSettingsRepository(private val api: UserSettingsApi) {

    suspend fun getUserSettings(): Result<UserSettingsDto> = try {
        val response = api.getUserSettings()
        Result.success(response)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateUserSettings(settings: UserSettingsDto): Result<Unit> = try {
        api.updateUserSettings(settings)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}