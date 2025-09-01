package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.features.users.data.LoggedDTO
import com.bellako.kiwi.features.users.data.LoginDTO

class UsersRepository(
    private val api: IUsersAPI,
) {
    suspend fun signup(dto: LoginDTO): Result<String> =
        runCatching {
            api.signup(dto)["message"] ?: return Result.failure(
                Exception(api.signup(dto)["error"]),
            )
        }

    suspend fun login(dto: LoginDTO): Result<LoggedDTO> =
        runCatching {
            api.login(dto)
        }
}
