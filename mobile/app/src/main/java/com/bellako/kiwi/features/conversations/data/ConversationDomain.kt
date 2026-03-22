package com.bellako.kiwi.features.conversations.data

import com.bellako.kiwi.common.services.ScriptVariableResolver
import kotlinx.serialization.Serializable

@Serializable
enum class ConversationType {
    FULL,
    SMALL,
}

@Serializable
enum class NextEventType {
    CONVERSATION,
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
    val backgroundId: Long? = null,
    val fxId: Long? = null,
    val dark: Boolean = false,
    val delayStartMs: Int? = null,
    val delayEndMs: Int? = null,
    val dialog: String,
    val dialogM: String,
    val dialogW: String,
    val nextEvent: NextEventType,
    val eventId: Long? = null,
    val fallbackEventId: Long? = null,
    val incidenceForNextEvent: String? = null,
    val options: List<ConversationOptionDomain> = emptyList(),
    val onCompletedEvent: String,
    val onCompletedEntityId: Int,
) {
    suspend fun readDialog(resolver: ScriptVariableResolver): String {
        val regex = Regex("@[a-zA-Z0-9_]+")
        var result = dialog

        regex.findAll(dialog).forEach { match ->
            val variableName = match.value.removePrefix("@")
            val value = resolver.getValue(variableName)
            result = result.replace(match.value, value)
        }

        return result
    }
}
