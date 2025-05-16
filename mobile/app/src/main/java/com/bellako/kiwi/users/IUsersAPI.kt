package com.bellako.kiwi.users

import retrofit2.http.Body
import retrofit2.http.POST

interface IUsersAPI {
    @POST("api/public/signup")
    fun signup(@Body dto: UsersDTO) : Result<Unit>

    @POST("api/public/login")
    fun login(@Body dto: UsersDTO) : Result<String>
}