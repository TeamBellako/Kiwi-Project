package com.bellako.kiwi.features.conversations.data

import kotlinx.serialization.Serializable

@Serializable
enum class ConversationType {
    FULL,
    SMALL,
}

@Serializable
enum class NextEventType {
    CONVERSATION,
    BATTLE,
    END,
}

@Serializable
data class ConversationOptionDomain(
    val id: Long? = null,
    val text: String,
    val textM: String,
    val textW: String,
    val nextEventId: Long,
    val cost: Int? = null,
)

@Serializable
data class ConversationDomain(
    val id: Long? = null,
    val name: String,
    val type: ConversationType,
    val spriteId: Long,
    val expresionId: Long,
    val backgroundId: Long,
    val fxId: Long? = null,
    val dark: Int,
    val delayStartMs: Int? = null,
    val delayEndMs: Int? = null,
    val dialog: String,
    val dialogM: String,
    val dialogW: String,
    val nextEvent: NextEventType,
    val eventId: Long,
    val options: List<ConversationOptionDomain> = emptyList(),
)
