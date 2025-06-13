package com.bellako.kiwi.features.users

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IUsersAPI {
    @POST("api/public/signup")
    suspend fun signup(@Body dto: UsersDTO) : Response<Unit>

    @POST("api/public/login")
    suspend fun login(@Body dto: UsersDTO) : Map<String, String>
}