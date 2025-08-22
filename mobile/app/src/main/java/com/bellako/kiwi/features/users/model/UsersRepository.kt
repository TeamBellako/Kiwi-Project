package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.features.users.data.UsersDTO

class UsersRepository(
    private val api: IUsersAPI,
) {
    suspend fun signup(dto: UsersDTO): Result<Unit> = runCatching { api.signup(dto) }

    suspend fun login(dto: UsersDTO): Result<String> =
        runCatching {
            api.login(dto)["jwt"] ?: return Result.failure(
                Exception("Missing JWT in response"),
            )
        }
}
