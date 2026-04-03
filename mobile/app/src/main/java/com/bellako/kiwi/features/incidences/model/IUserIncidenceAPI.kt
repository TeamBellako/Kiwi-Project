package com.bellako.kiwi.features.incidences.model

import com.bellako.kiwi.features.incidences.data.UserIncidenceDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IUserIncidenceAPI {
    @GET("api/user_incidences/{incidenceName}")
    suspend fun getUserIncidence(
        @Path("incidenceName") incidenceName: String,
    ): Boolean

    @POST("api/user_incidences")
    suspend fun updateOrCreateUserIncidence(
        @Body dto: UserIncidenceDTO,
    )
}
