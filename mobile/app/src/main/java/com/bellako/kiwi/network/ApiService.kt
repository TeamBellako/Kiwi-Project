package com.bellako.kiwi.network

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("actuator/health")
    suspend fun ping(): Response<Unit>
}