package com.bellako.kiwi.userSettings

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface IUserSettingsAPI {
    @GET("api/user/settings")
    suspend fun getUserSettings(): UserSettingsDTO

    @PUT("api/user/settings")
    suspend fun updateUserSettings(@Body settings: UserSettingsDTO)
}
