package com.bellako.kiwi.users

import retrofit2.http.Body
import retrofit2.http.POST

interface IUsersAPI {
    @POST("api/public/signup")
    suspend fun signup(@Body dto: UsersDTO)

    @POST("api/public/login")
    suspend fun login(@Body dto: UsersDTO)
}