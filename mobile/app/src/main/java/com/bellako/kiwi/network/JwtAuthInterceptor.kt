package com.bellako.kiwi.network

import okhttp3.Interceptor
import okhttp3.Response

class JwtAuthInterceptor : Interceptor {

    //TODO: Remove once accounts are implemented
    private val hardCodedJwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmaW5uQHRoZWh1bWFuLmNvbSIsImlhdCI6MTc0NzA0MjcwMywiZXhwIjoxNzQ3MDQ2MzAzfQ.9Cq7jVa7f7fnkT28-oHkUlxBxo6S20FVj71Z4loo1NY"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", hardCodedJwt)
            .build()

        return chain.proceed(newRequest)
    }
}
