package com.bellako.kiwi.users

class UsersRepository (private val api: IUsersAPI){
    suspend fun signup(dto: UsersDTO) : Result<Unit> =
        runCatching { return api.signup(dto) }

    suspend fun login(dto: UsersDTO): Result<String> = runCatching {
        val response = api.login(dto)
        response["jwt"] ?: throw Exception("Missing JWT in response")
    }
}