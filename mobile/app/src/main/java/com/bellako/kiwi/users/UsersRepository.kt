package com.bellako.kiwi.users

class UsersRepository (private val api: IUsersAPI){
    fun signup(dto: UsersDTO) : Result<Unit> =
        runCatching { return api.signup(dto) }

    fun login(dto: UsersDTO) : Result<String> =
        runCatching { return api.login(dto) }
}