package com.bellako.kiwi.features.metrics.model

import com.bellako.kiwi.features.metrics.data.MetricsDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface IMetricsAPI {
    @POST("api/user/metrics")
    suspend fun createMetrics(
        @Body dto: MetricsDTO,
    ): MetricsDTO

    @PUT("api/user/metrics")
    suspend fun updateMetrics(
        @Body dto: MetricsDTO,
    ): MetricsDTO

    @GET("api/user/metrics")
    suspend fun getMetricsByDate(
        @Query("date") date: String,
    ): MetricsDTO?
}
