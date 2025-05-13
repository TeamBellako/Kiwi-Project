package com.bellako.kiwi.network

import okhttp3.Interceptor
import okhttp3.Response

class JwtAuthInterceptor : Interceptor {

    //TODO: Remove once accounts are implemented
    private val hardCodedJwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmaW5uQHRoZWh1bWFuLmNvbSIsImlhdCI6MTc0NzEyMzUyNSwiZXhwIjoxNzQ3MTI3MTI1fQ.r3tGsb16z-pHDdsJzekhd5pB9HKYmEIeU3J-v4Ldtvc"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", hardCodedJwt)
            .build()

        return chain.proceed(newRequest)
    }
}
