package com.bellako.kiwi.network

import okhttp3.Interceptor
import okhttp3.Response

class JwtAuthInterceptor : Interceptor {

    //TODO: Remove once accounts are implemented
    private val hardCodedJwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmaW5uQHRoZWh1bWFuLmNvbSIsInJvbGVzIjpbIlVTRVIiXSwiaWF0IjoxNzQ3MTMxODc5LCJleHAiOjE3NDcxMzU0Nzl9.3Wz73Chmhib46l7HPDKW6WGx1Jy2Oy4PwK13BeuTxyg"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", hardCodedJwt)
            .build()

        return chain.proceed(newRequest)
    }
}
