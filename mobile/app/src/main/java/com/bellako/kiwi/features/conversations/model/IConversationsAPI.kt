package com.bellako.kiwi.features.conversations.model

import com.bellako.kiwi.features.conversations.data.ConversationDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IConversationsAPI {
    @GET("api/conversations/{id}")
    suspend fun getConversationById(
        @Path("id") id: Long,
    ): ConversationDTO

    @POST("api/conversations/options")
    suspend fun saveConversationOptions(
        @Body optionIds: List<Long>,
    )
}
