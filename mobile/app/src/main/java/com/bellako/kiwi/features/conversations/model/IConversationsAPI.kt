package com.bellako.kiwi.features.conversations.model

import com.bellako.kiwi.features.conversations.data.ConversationDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface IConversationsAPI {
    @GET("/api/conversations/{id}")
    suspend fun getConversationById(@Path("id") id: Long): ConversationDTO
}
