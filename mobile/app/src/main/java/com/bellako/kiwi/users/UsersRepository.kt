package com.bellako.kiwi.users

import retrofit2.HttpException

class UsersRepository(private val api: IUsersAPI) {

    suspend fun signup(dto: UsersDTO): Result<Unit> {
        return try {
            val response = api.signup(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(dto: UsersDTO): Result<String> = runCatching {
        val response = api.login(dto)
        response["jwt"] ?: throw Exception("Missing JWT in response")
    }
}