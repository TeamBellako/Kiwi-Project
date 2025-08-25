package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.features.users.data.UsersDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface IUsersAPI {
    @POST("api/public/signup")
    suspend fun signup(
        @Body dto: UsersDTO,
    ): Map<String, String>

    @POST("api/public/login")
    suspend fun login(
        @Body dto: UsersDTO,
    ): Map<String, String>
}
