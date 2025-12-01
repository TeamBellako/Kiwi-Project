package com.bellako.kiwi.features.quests.data

class SubquestDataMapper {
    fun toDomain(dto: SubquestDTO): SubquestDomain =
        SubquestDomain(
            id = dto.id,
            name = dto.name,
            experience = dto.experience,
            order = dto.order,
            status = SubquestStatus.valueOf(dto.status),
        )

    fun toDto(domain: SubquestDomain): SubquestDTO =
        SubquestDTO(
            id = domain.id,
            name = domain.name,
            experience = domain.experience,
            order = domain.order,
            status = domain.status.name,
        )
}
