package com.bellako.kiwi.features.users.tests

import com.bellako.kiwi.features.users.data.UsersDTO

object UsersTestFactory {
    fun validUsersDTO(): UsersDTO =
        UsersDTO(
            email = "finn@thehuman.com",
            password = "Math3matic!",
        )

    fun invalidUsersDTO(): UsersDTO =
        UsersDTO(
            email = "football.com",
            password = "kk",
        )
}
