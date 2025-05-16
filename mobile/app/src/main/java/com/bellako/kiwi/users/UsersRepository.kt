package com.bellako.kiwi.users

class UsersRepository (private val api: IUsersAPI){
    suspend fun signup(dto: UsersDTO) : Result<Unit> =
        runCatching { return api.signup(dto) }

    suspend fun login(dto: UsersDTO) : Result<String> =
        runCatching { return api.login(dto) }
}