package com.bellako.kiwi

import com.bellako.kiwi.features.users.data.LoggedDTO
import com.bellako.kiwi.features.users.data.LoginDTO
import com.bellako.kiwi.features.users.data.UserPointsDTO
import com.bellako.kiwi.features.users.model.IUsersAPI
import com.bellako.kiwi.features.users.model.UsersRepository

fun UsersFakeRepository(): UsersRepository =
    UsersRepository(
        object : IUsersAPI {
            override suspend fun signup(dto: LoginDTO): Map<String, String> = emptyMap()

            override suspend fun login(dto: LoginDTO): LoggedDTO = LoggedDTO("", "")

            override suspend fun getMyUserPoints(): UserPointsDTO = UserPointsDTO(0, 0)
        },
    )

