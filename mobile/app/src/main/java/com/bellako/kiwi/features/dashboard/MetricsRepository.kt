package com.bellako.kiwi.features.dashboard

import retrofit2.HttpException
import retrofit2.Response

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

    suspend fun getMetricsByDateAndUser(email: String, date: String) : Result<MetricsDTO?> {
        return try {
            val response: Response<MetricsDTO> = api.getMetricsByDateAndUser(email, date)
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