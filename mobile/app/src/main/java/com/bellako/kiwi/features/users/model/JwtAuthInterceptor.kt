package com.bellako.kiwi.features.users.model

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class JwtAuthInterceptor
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()

            val token = authRepository.getJwtToken()

            val newRequest =
                originalRequest
                    .newBuilder()
                    .apply {
                        if (token?.isNotEmpty() == true) {
                            addHeader("Authorization", "Bearer $token")
                        }
                    }.build()

            val url = newRequest.url.toString()
            android.util.Log.d("ApiDebug", "Llamada $url")

            val response = chain.proceed(newRequest)

            android.util.Log.d("ApiDebug", "Llamada $url -> código ${response.code}")

            return response
        }
    }
