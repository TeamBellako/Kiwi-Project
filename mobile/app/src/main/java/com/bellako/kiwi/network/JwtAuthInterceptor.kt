package com.bellako.kiwi.network

import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class JwtAuthInterceptor @Inject constructor() : Interceptor {
    private var jwtToken : String = ""

    fun setJwtToken(jwtToken : String) { this.jwtToken = jwtToken }

    fun isJwtTokenSet() = jwtToken.isNotEmpty()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $jwtToken")
            .build()

        return chain.proceed(newRequest)
    }
}
