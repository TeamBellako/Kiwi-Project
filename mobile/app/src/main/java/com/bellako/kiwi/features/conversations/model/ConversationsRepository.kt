package com.bellako.kiwi.features.conversations.model

import com.bellako.kiwi.features.conversations.data.ConversationDataMapper
import com.bellako.kiwi.features.conversations.data.ConversationDomain

class ConversationsRepository(
    private val api: IConversationsAPI,
) {
    suspend fun getById(id: Long): ConversationDomain = ConversationDataMapper.toDomain(api.getConversationById(id))

    suspend fun saveOptions(optionIds: List<Long>) = api.saveConversationOptions(optionIds)
}
