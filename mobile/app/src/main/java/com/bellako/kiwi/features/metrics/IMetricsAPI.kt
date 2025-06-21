package com.bellako.kiwi.features.metrics

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface IMetricsAPI {
    @POST("api/user/metrics")
    suspend fun createMetrics(@Body dto: MetricsDTO): Response<Unit>

    @PUT("api/user/metrics")
    suspend fun updateMetrics(@Body dto: MetricsDTO): Response<Unit>

    @GET("api/user/metrics")
    suspend fun getMetricsByDateAndUser(
        @Query("email") email: String,
        @Query("date") date: String
    ): Response<MetricsDTO>
}
