package com.kiwi.network

import com.kiwi.BuildConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object RetrofitClient { private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.MOBILE_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: APIService = retrofit.create(APIService::class.java)
}

interface APIService {
    @GET("api/hello/{id}")
    suspend fun getMessage(@Path("id") id: Int): Response<HelloResponse>
}

data class HelloResponse(
    val message: String
)