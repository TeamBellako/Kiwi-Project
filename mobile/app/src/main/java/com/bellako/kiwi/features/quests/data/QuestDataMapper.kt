package com.bellako.kiwi.features.quests.data

object QuestDataMapper {
    fun toDomain(dto: QuestDTO): QuestDomain =
        QuestDomain(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            experience = dto.experience,
            status = QuestStatus.valueOf(dto.status),
            subquests = dto.subquests.map { toDomain(it) },
        )

    fun toDomain(dto: SubquestDTO): SubquestDomain =
        SubquestDomain(
            id = dto.id,
            name = dto.name,
            experience = dto.experience,
            order = dto.order,
            status = SubquestStatus.valueOf(dto.status),
        )

    fun toDto(domain: QuestDomain): QuestDTO =
        QuestDTO(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            experience = domain.experience,
            status = domain.status.toString(),
            subquests = domain.subquests.map { toDto(it) },
        )

    fun toDto(domain: SubquestDomain): SubquestDTO =
        SubquestDTO(
            id = domain.id,
            name = domain.name,
            experience = domain.experience,
            order = domain.order,
            status = domain.status.toString(),
        )
}
