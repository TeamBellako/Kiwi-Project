package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.features.users.data.UsersDTO
import retrofit2.HttpException

class UsersRepository(
    private val api: IUsersAPI,
) {
    suspend fun signup(dto: UsersDTO): Result<Unit> =
        try {
            val response = api.signup(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: HttpException) {
            Result.failure(e)
        }

    suspend fun login(dto: UsersDTO): Result<String> {
        return try {
            val result = api.login(dto)
            val jwt =
                result["jwt"] ?: return Result.failure(
                    Exception("Missing JWT in response"),
                )
            Result.success(jwt)
        } catch (e: HttpException) {
            Result.failure(e)
        }
    }
}
