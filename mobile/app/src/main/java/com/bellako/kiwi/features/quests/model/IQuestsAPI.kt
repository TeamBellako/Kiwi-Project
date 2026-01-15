package com.bellako.kiwi.features.quests.model

import com.bellako.kiwi.features.quests.data.QuestDTO
import com.bellako.kiwi.features.quests.data.SubquestDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IQuestsAPI {
    @GET("api/quests/active")
    suspend fun getActiveQuests(): List<QuestDTO>

    // TODO: IF WE EVER NEED THIS, CHANGE BACKEND AND LOAD ALL QUESTS AND THEN FILTER LIKE IN THE SKILLS STATE
    @GET("api/quests/completed")
    suspend fun getCompletedQuests(): List<QuestDTO>

    @GET("api/quests/{questId}")
    suspend fun getQuest(
        @Path("questId") questId: Int,
    ): QuestDTO

    @GET("api/quests/subquests/{subquestId}")
    suspend fun getSubquest(
        @Path("subquestId") subquestId: Int,
    ): SubquestDTO

    @POST("api/quests/{questId}/give")
    suspend fun giveQuest(
        @Path("questId") questId: Int,
    ): QuestDTO

    @POST("api/quests/subquests/{subquestId}/complete")
    suspend fun completeSubquest(
        @Path("subquestId") subquestId: Int,
    ): QuestDTO

    @POST("api/quests/subquests/{subquestId}/fail")
    suspend fun failSubquest(
        @Path("subquestId") subquestId: Int,
    ): QuestDTO
}
