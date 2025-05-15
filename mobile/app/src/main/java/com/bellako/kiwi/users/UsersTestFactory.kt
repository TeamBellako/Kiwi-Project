package com.bellako.kiwi.users

object UsersTestFactory {
    fun validUsersDTO() : UsersDTO {
        return UsersDTO(
            email = "finn@thehuman.com",
            password = "Math3matical!"
        )
    }

    fun invalidUsersDTO() : UsersDTO {
        return UsersDTO(
            email = "bmolovesfootball.com",
            password = "kk"
        )
    }
}