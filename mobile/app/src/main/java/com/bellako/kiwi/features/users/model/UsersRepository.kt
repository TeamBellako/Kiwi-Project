package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.features.users.data.UsersDTO
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

    suspend fun login(dto: UsersDTO): Result<String> {
        return try {
            val result = api.login(dto)
            val jwt = result["jwt"] ?: return Result.failure(
                Exception("Missing JWT in response")
            )
            Result.success(jwt)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}