package com.bellako.kiwi.features.metrics

import com.bellako.kiwi.features.users.Email
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDate

class MetricsRepository(private val api: IMetricsAPI) {
    suspend fun createMetrics(dto: MetricsDTO): Result<Unit> {
        return try {
            val response = api.createMetrics(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMetrics(dto: MetricsDTO): Result<Unit> {
        return try {
            val response = api.updateMetrics(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMetricsByDateAndUser(email: Email, date: LocalDate) : Result<MetricsDTO?> {
        return try {
            val response: Response<MetricsDTO> = api.getMetricsByDateAndUser(email.value, date.toString())
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}