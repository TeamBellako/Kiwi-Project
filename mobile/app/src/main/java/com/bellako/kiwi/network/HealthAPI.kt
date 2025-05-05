package com.bellako.kiwi.network

import com.bellako.kiwi.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

private fun createShortTimeoutOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()
}

interface HealthApiService {
    @GET("actuator/health")
    suspend fun ping(): Response<Unit>
}

fun createHealthApiService(): HealthApiService {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.MOBILE_API_URL)
        .client(createShortTimeoutOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(HealthApiService::class.java)
}
