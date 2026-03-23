package com.bellako.kiwi.features.conversations.data

object ConversationDataMapper {
    fun toDomain(dto: ConversationDTO): ConversationDomain =
        ConversationDomain(
            id = dto.id,
            name = dto.name,
            type = dto.type,
            spriteId = dto.spriteId,
            expresionId = dto.expresionId,
            backgroundId = dto.backgroundId,
            fxId = dto.fxId,
            dark = dto.dark,
            delayStartMs = dto.delayStartMs,
            delayEndMs = dto.delayEndMs,
            dialog = dto.dialog,
            dialogM = dto.dialogM,
            dialogW = dto.dialogW,
            nextEvent = dto.nextEvent,
            eventId = dto.eventId,
            fallbackEventId = dto.fallbackEventId,
            incidenceForNextEvent = dto.incidenceForNextEvent,
            incidenceNameToSet = dto.incidenceNameToSet,
            incidenceValueToSet = dto.incidenceValueToSet,
            options = dto.options.map { toDomain(it) },
            onCompletedEvent = dto.onCompletedEvent,
            onCompletedEntityId = dto.onCompletedEntityId,
        )

    fun toDomain(dto: ConversationOptionDTO): ConversationOptionDomain =
        ConversationOptionDomain(
            id = dto.id,
            text = dto.text,
            textM = dto.textM,
            textW = dto.textW,
            nextEventId = dto.nextEventId,
            cost = dto.cost,
        )

    fun toDTO(domain: ConversationDomain): ConversationDTO =
        ConversationDTO(
            id = domain.id,
            name = domain.name,
            type = domain.type,
            spriteId = domain.spriteId,
            expresionId = domain.expresionId,
            backgroundId = domain.backgroundId,
            fxId = domain.fxId,
            dark = domain.dark,
            delayStartMs = domain.delayStartMs,
            delayEndMs = domain.delayEndMs,
            dialog = domain.dialog,
            dialogM = domain.dialogM,
            dialogW = domain.dialogW,
            nextEvent = domain.nextEvent,
            eventId = domain.eventId,
            fallbackEventId = domain.fallbackEventId,
            incidenceForNextEvent = domain.incidenceForNextEvent,
            incidenceNameToSet = domain.incidenceNameToSet,
            incidenceValueToSet = domain.incidenceValueToSet,
            options = domain.options.map { toDTO(it) },
            onCompletedEvent = domain.onCompletedEvent,
            onCompletedEntityId = domain.onCompletedEntityId,
        )

    fun toDTO(domain: ConversationOptionDomain): ConversationOptionDTO =
        ConversationOptionDTO(
            id = domain.id,
            conversationId = null,
            text = domain.text,
            textM = domain.textM,
            textW = domain.textW,
            nextEventId = domain.nextEventId,
            cost = domain.cost,
        )
}
