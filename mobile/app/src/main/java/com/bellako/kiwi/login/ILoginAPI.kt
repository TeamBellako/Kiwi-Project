package com.bellako.kiwi.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ILoginAPI {
    @POST("api/public/signup")
    suspend fun signup(@Body dto: LoginDTO) : Response<Unit>

    @POST("api/public/login")
    suspend fun login(@Body dto: LoginDTO) : Map<String, String>
}