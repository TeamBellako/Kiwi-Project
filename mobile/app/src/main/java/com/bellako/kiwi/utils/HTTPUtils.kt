package com.bellako.kiwi.utils

import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

object HTTPUtils {
    fun createFakeHttpException(code: Int): HttpException {
        val response = Response.error<Any>(
            code,
            "Error $code".toResponseBody(null)
        )
        return HttpException(response)
    }
}