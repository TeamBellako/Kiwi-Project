package com.bellako.kiwi.common.model

import com.bellako.kiwi.common.data.UIState
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

object HTTPUtils {
    fun createFakeHttpException(code: Int): HttpException {
        val response = Response.error<Any>(
            code,
            "Error $code".toResponseBody(null)
        )
        return HttpException(response)
    }

    fun extractHttpExceptionMessage(exception: HttpException) : String {
        val errorBody = exception.response()?.errorBody()?.string()
        val errorMessage = parseErrorMessage(errorBody)

        return errorMessage ?: "Something went wrong"
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

    fun mapExceptionToUIState(e: Throwable): UIState<Unit> {
        return when (e) {
            is HttpException -> {
                if (e.code() >= 500) UIState.GeneralError
                else UIState.Error(parseErrorMessage(e.response()?.errorBody()?.string())!!)
            }
            is IOException -> UIState.GeneralError
            else -> UIState.GeneralError
        }
    }
}