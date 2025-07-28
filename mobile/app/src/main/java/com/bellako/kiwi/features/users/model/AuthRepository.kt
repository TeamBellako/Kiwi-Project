package com.bellako.kiwi.features.users.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private var jwtToken: String? = ""

    fun setJwtToken(token: String) { jwtToken = token }

    fun getJwtToken(): String? = jwtToken

    fun isJwtTokenSet(): Boolean = jwtToken?.isNotEmpty() == true
}
