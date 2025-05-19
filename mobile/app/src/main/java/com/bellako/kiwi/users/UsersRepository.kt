package com.bellako.kiwi.users

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.HttpException

class UsersRepository (private val api: IUsersAPI){
    suspend fun signup(dto: UsersDTO): Result<Unit> {
        return try {
            val response = api.signup(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)

                Result.failure(Exception(errorMessage ?: "Unknown error"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody)

            Result.failure(Exception(errorMessage ?: e.message()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(dto: UsersDTO): Result<String> = runCatching {
        val response = api.login(dto)
        response["jwt"] ?: throw Exception("Missing JWT in response")
    }

    fun parseErrorMessage(json: String?): String? {
        if (json.isNullOrBlank()) return null

        return try {
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
            val adapter = moshi.adapter<Map<String, String>>(type)
            val map = adapter.fromJson(json)
            map?.get("error")
        } catch (ex: Exception) {
            null
        }
    }
}