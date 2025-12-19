package com.bellako.kiwi.features.quests.data

object QuestDataMapper {
    fun toDomain(dto: QuestDTO): QuestDomain =
        QuestDomain(
            id = dto.questId,
            name = dto.name,
            description = dto.description,
            experience = dto.experience,
            status = QuestStatus.valueOf(dto.status),
            subquests = dto.subquests.map { toDomain(it) },
        )

    fun toDomain(dto: SubquestDTO): SubquestDomain =
        SubquestDomain(
            id = dto.subquestId,
            name = dto.name,
            experience = dto.experience,
            order = dto.order,
            status = SubquestStatus.valueOf(dto.status),
        )

    fun toDto(domain: QuestDomain): QuestDTO =
        QuestDTO(
            questId = domain.id,
            name = domain.name,
            description = domain.description,
            experience = domain.experience,
            status = domain.status.toString(),
            subquests = domain.subquests.map { toDto(it) },
        )

    fun toDto(domain: SubquestDomain): SubquestDTO =
        SubquestDTO(
            subquestId = domain.id,
            name = domain.name,
            experience = domain.experience,
            order = domain.order,
            status = domain.status.toString(),
        )
}
