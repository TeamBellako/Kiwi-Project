package com.bellako.kiwi.features.conversations.data

import kotlinx.serialization.Serializable

@Serializable
data class ConversationOptionDTO(
    val id: Long? = null,
    val conversationId: Long? = null,
    val text: String,
    val textM: String,
    val textW: String,
    val nextEventId: Long,
    val cost: Int? = null,
    val incidenceToShow: String? = null,
)

@Serializable
data class ConversationDTO(
    val id: Long? = null,
    val name: String,
    val type: ConversationType,
    val spriteId: Long,
    val expresionId: Long,
    val backgroundId: Long? = null,
    val fxId: Long? = null,
    val dark: Boolean = false,
    val delayStartMs: Int? = null,
    val delayEndMs: Int? = null,
    val dialog: String,
    val dialogM: String,
    val dialogW: String,
    val nextEvent: NextEventType,
    val incidenceForNextEvent: String? = null,
    val eventId: Long? = null,
    val fallbackEventId: Long? = null,
    val incidenceNameToSet: String? = null,
    val incidenceValueToSet: Boolean = true,
    val options: List<ConversationOptionDTO> = emptyList(),
    val onCompletedEvent: String,
    val onCompletedEntityId: Int,
)
