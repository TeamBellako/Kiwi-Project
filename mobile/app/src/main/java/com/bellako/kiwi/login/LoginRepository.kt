package com.bellako.kiwi.login

import retrofit2.HttpException

class LoginRepository(private val api: ILoginAPI) {

    suspend fun signup(dto: LoginDTO): Result<Unit> {
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

    suspend fun login(dto: LoginDTO): Result<String> {
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