package com.bellako.kiwi.features.personality.model

import com.bellako.kiwi.features.personality.data.PersonalityBuildDTO
import com.bellako.kiwi.features.personality.data.PersonalityDTO
import com.bellako.kiwi.features.personality.data.PersonalityUserNameDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IPersonalityAPI {
    @GET("api/user/personality")
    suspend fun getPersonality(): PersonalityDTO

    @POST("api/user/personality/realName")
    suspend fun updateRealName(
        @Body dto: PersonalityUserNameDTO,
    )

    @POST("api/user/personality/knightName")
    suspend fun updateKnightName(
        @Body dto: PersonalityUserNameDTO,
    )

    @POST("api/user/personality/build")
    suspend fun updateBuild(
        @Body dto: PersonalityBuildDTO,
    )
}
