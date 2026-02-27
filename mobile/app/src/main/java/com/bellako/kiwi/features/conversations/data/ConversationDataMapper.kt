package com.bellako.kiwi.features.conversations.data

object ConversationDataMapper {
    /**
     * Convierte DTO a Domain con validación de datos
     */
    fun toDomain(dto: ConversationDTO): Result<ConversationDomain> =
        runCatching {
            // Validaciones de datos críticos
            require(dto.name.isNotBlank()) { "Name cannot be blank" }
            require(dto.spriteId > 0) { "SpriteId must be positive" }
            require(dto.expresionId > 0) { "ExpresionId must be positive" }
            require(dto.dialog.isNotBlank()) { "Dialog cannot be blank" }
            require(dto.dialogM.isNotBlank()) { "DialogM cannot be blank" }
            require(dto.dialogW.isNotBlank()) { "DialogW cannot be blank" }

            // Validar delays si están presentes
            dto.delayStartMs?.let { require(it >= 0) { "DelayStartMs must be non-negative" } }
            dto.delayEndMs?.let { require(it >= 0) { "DelayEndMs must be non-negative" } }
            dto.fxId?.let { require(it > 0) { "FxId must be positive if provided" } }

            // Validar options
            val validatedOptions =
                dto.options.mapNotNull { optionDto ->
                    toDomain(optionDto).getOrNull()
                }

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
                options = validatedOptions,
            )
        }

    /**
     * Convierte ConversationOptionDTO a Domain con validación
     */
    fun toDomain(dto: ConversationOptionDTO): Result<ConversationOptionDomain> =
        runCatching {
            require(dto.text.isNotBlank()) { "Option text cannot be blank" }
            require(dto.textM.isNotBlank()) { "Option textM cannot be blank" }
            require(dto.textW.isNotBlank()) { "Option textW cannot be blank" }
            require(dto.nextEventId > 0) { "NextEventId must be positive" }
            dto.cost?.let { require(it >= 0) { "Cost must be non-negative if provided" } }

            ConversationOptionDomain(
                id = dto.id,
                text = dto.text,
                textM = dto.textM,
                textW = dto.textW,
                nextEventId = dto.nextEventId,
                cost = dto.cost,
            )
        }

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
            options = domain.options.map { toDTO(it) },
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
