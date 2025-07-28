package com.bellako.kiwi.features.users.tests

import com.bellako.kiwi.features.users.data.UsersDTO

object UsersTestFactory {
    fun validUsersDTO() : UsersDTO {
        return UsersDTO(
            email = "finn@thehuman.com",
            password = "Math3matical!"
        )
    }

    fun incorrectPasswordUsersDTO() : UsersDTO {
        return UsersDTO(
            email = "finn@thehuman.com",
            password = "Math3maticalawdawdwad!"
        )
    }

    fun invalidUsersDTO() : UsersDTO {
        return UsersDTO(
            email = "bmolovesfootball.com",
            password = "kk"
        )
    }
}