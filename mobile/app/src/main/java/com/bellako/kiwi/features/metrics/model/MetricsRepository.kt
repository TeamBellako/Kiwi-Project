package com.bellako.kiwi.features.metrics.model

import com.bellako.kiwi.features.metrics.data.MetricsDTO
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDate

class MetricsRepository(
    private val api: IMetricsAPI,
) {
    suspend fun createMetrics(dto: MetricsDTO): Result<Unit> =
        try {
            val response = api.createMetrics(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: HttpException) {
            Result.failure(e)
        }

    suspend fun updateMetrics(dto: MetricsDTO): Result<Unit> =
        try {
            val response = api.updateMetrics(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: HttpException) {
            Result.failure(e)
        }

    suspend fun getMetricsByDate(date: LocalDate): Result<MetricsDTO?> =
        try {
            val response: Response<MetricsDTO> = api.getMetricsByDate(date.toString())
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: HttpException) {
            Result.failure(e)
        }
}
