package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.features.users.data.LoggedDTO
import com.bellako.kiwi.features.users.data.LoginDTO
import com.bellako.kiwi.features.users.data.UserPointsDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IUsersAPI {
    @POST("api/public/signup")
    suspend fun signup(
        @Body dto: LoginDTO,
    ): Map<String, String>

    @POST("api/public/login")
    suspend fun login(
        @Body dto: LoginDTO,
    ): LoggedDTO

    @GET("api/user/points")
    suspend fun getMyUserPoints(): UserPointsDTO
}
