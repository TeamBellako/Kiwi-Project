package com.bellako.kiwi.login

object LoginTestFactory {
    fun validLoginDTO() : LoginDTO {
        return LoginDTO(
            email = "finn@thehuman.com",
            password = "Math3matical!"
        )
    }

    fun incorrectPasswordLoginDTO() : LoginDTO {
        return LoginDTO(
            email = "finn@thehuman.com",
            password = "Math3maticalawdawdwad!"
        )
    }

    fun invalidLoginDTO() : LoginDTO {
        return LoginDTO(
            email = "bmolovesfootball.com",
            password = "kk"
        )
    }
}