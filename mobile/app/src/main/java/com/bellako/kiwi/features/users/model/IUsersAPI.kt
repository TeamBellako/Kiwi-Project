package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.features.users.data.LoggedDTO
import com.bellako.kiwi.features.users.data.LoginDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface IUsersAPI {
    @POST("api/public/signup")
    suspend fun signup(
        @Body dto: LoginDTO,
    ): Map<String, String>

    @POST("api/public/login")
    suspend fun login(
        @Body dto: LoginDTO,
    ): LoggedDTO
}
