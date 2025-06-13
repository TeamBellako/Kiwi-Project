package com.bellako.kiwi.services.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private var jwtToken: String? = ""

    fun setJwtToken(token: String) { jwtToken = token }

    fun getJwtToken(): String? = jwtToken

    fun isJwtTokenSet(): Boolean = jwtToken?.isNotEmpty() == true
}
