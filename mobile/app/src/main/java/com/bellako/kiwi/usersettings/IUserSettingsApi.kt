package com.bellako.kiwi.usersettings

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface IUserSettingsApi {
    @GET("api/settings/me")
    suspend fun getUserSettings(): UserSettingsDto

    @PUT("api/settings")
    suspend fun updateUserSettings(@Body settings: UserSettingsDto)
}
