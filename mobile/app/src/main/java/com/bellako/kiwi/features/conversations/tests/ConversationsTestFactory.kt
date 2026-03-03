package com.bellako.kiwi.features.conversations.tests

import com.bellako.kiwi.features.conversations.data.ConversationDomain
import com.bellako.kiwi.features.conversations.data.ConversationOptionDomain
import com.bellako.kiwi.features.conversations.data.ConversationType
import com.bellako.kiwi.features.conversations.data.NextEventType

@Suppress("MagicNumber", "LongParameterList")
object ConversationsTestFactory {
    fun validConversationDomain(
        id: Long = 1L,
        name: String = "Test Conversation",
        type: ConversationType = ConversationType.FULL,
        spriteId: Long = 1L,
        expresionId: Long = 1L,
        backgroundId: Long? = 1L,
        fxId: Long? = null,
        dark: Boolean = false,
        delayStartMs: Int? = null,
        delayEndMs: Int? = null,
        dialog: String = "This is the main dialogue text",
        dialogM: String = "This is the male dialogue text",
        dialogW: String = "This is the female dialogue text",
        nextEvent: NextEventType = NextEventType.END,
        eventId: Long? = null,
        options: List<ConversationOptionDomain> = emptyList(),
    ): ConversationDomain =
        ConversationDomain(
            id = id,
            name = name,
            type = type,
            spriteId = spriteId,
            expresionId = expresionId,
            backgroundId = backgroundId,
            fxId = fxId,
            dark = dark,
            delayStartMs = delayStartMs,
            delayEndMs = delayEndMs,
            dialog = dialog,
            dialogM = dialogM,
            dialogW = dialogW,
            nextEvent = nextEvent,
            eventId = eventId,
            options = options,
        )

    fun validConversationOption(
        id: Long = 10L,
        text: String = "Option text",
        textM: String = "Option text M",
        textW: String = "Option text W",
        nextEventId: Long = 2L,
        cost: Int? = null,
    ): ConversationOptionDomain =
        ConversationOptionDomain(
            id = id,
            text = text,
            textM = textM,
            textW = textW,
            nextEventId = nextEventId,
            cost = cost,
        )

    /** Conversación que encadena a otra conversación (id 2L) */
    fun chainedConversationDomain(): ConversationDomain =
        validConversationDomain(
            id = 1L,
            nextEvent = NextEventType.CONVERSATION,
            eventId = 2L,
        )

    /** Conversación que termina sin ir a ningún sitio */
    fun endConversationDomain(): ConversationDomain =
        validConversationDomain(
            id = 2L,
            nextEvent = NextEventType.END,
            eventId = null,
        )

    /** Conversación con opciones donde cada opción navega a otra conversación */
    fun conversationWithOptions(): ConversationDomain =
        validConversationDomain(
            id = 3L,
            nextEvent = NextEventType.CONVERSATION,
            options =
                listOf(
                    validConversationOption(id = 10L, nextEventId = 4L),
                    validConversationOption(id = 11L, nextEventId = 4L),
                ),
        )

    /** Conversación de destino tras elegir una opción (END) */
    fun optionTargetConversationDomain(): ConversationDomain =
        validConversationDomain(
            id = 4L,
            nextEvent = NextEventType.END,
            eventId = null,
        )
}
