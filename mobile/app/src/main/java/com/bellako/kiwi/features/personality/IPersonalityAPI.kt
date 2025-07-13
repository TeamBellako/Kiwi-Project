package com.bellako.kiwi.features.personality

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface IPersonalityAPI {
    @GET("api/user/personality")
    suspend fun getPersonality(): PersonalityDTO

    @PUT("api/user/personality/realName")
    suspend fun updateRealName(@Body dto: PersonalityUserNameDTO)

    @PUT("api/user/personality/knightName")
    suspend fun updateKnightName(@Body dto: PersonalityUserNameDTO)
}
