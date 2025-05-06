package com.bellako.kiwi.userSettings.network

import com.bellako.kiwi.userSettings.types.UserSettingsDTO
import jakarta.inject.Singleton
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

@Singleton
interface IUserSettingsAPI {
    @GET("api/settings/me")
    suspend fun getUserSettings(): UserSettingsDTO

    @PUT("api/settings")
    suspend fun updateUserSettings(@Body settings: UserSettingsDTO)
}
