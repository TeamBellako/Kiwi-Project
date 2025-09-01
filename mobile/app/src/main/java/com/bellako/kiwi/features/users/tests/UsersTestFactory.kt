package com.bellako.kiwi.features.users.tests

import com.bellako.kiwi.features.users.data.LoggedDTO
import com.bellako.kiwi.features.users.data.LoginDTO
import com.bellako.kiwi.features.users.data.UsersDTO

object UsersTestFactory {
    fun validLoginDTO(): LoginDTO =
        LoginDTO(
            email = "finn@thehuman.com",
            password = "Math3matic!",
        )

    fun invalidLoginDTO(): LoginDTO =
        LoginDTO(
            email = "football.com",
            password = "kk",
        )

    private const val DATE = "2025-01-01"

    fun validLoggedDTO(): LoggedDTO =
        LoggedDTO(
            jwt = "test",
            registerDate = DATE,
        )

    fun validUsersDTO(): UsersDTO =
        UsersDTO(
            email = validLoginDTO().email,
            password = validLoginDTO().password,
            registerDate = DATE,
        )

    fun invalidUsersDTO(): UsersDTO =
        UsersDTO(
            email = invalidLoginDTO().email,
            password = invalidLoginDTO().password,
            registerDate = DATE,
        )
}
