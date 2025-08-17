package com.bellako.kiwi.features.settings.model

import com.bellako.kiwi.features.settings.data.SettingsDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ISettingsAPI {
    @GET("api/user/settings")
    suspend fun getSettings(): SettingsDTO

    @PUT("api/user/settings")
    suspend fun updateSettings(
        @Body settings: SettingsDTO,
    )
}
