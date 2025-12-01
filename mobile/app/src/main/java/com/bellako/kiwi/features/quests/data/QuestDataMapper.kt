package com.bellako.kiwi.features.quests.data

class QuestDataMapper(
    private val subquestMapper: SubquestDataMapper = SubquestDataMapper(),
) {
    fun toDomain(dto: QuestDTO): QuestDomain =
        QuestDomain(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            experience = dto.experience,
            status = QuestStatus.valueOf(dto.status),
            subquests = dto.subquests.map { subquestMapper.toDomain(it) },
        )

    fun toDto(domain: QuestDomain): QuestDTO =
        QuestDTO(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            experience = domain.experience,
            status = domain.status.name,
            subquests = domain.subquests.map { subquestMapper.toDto(it) },
        )
}
