package com.bellako.kiwi.features.tips.data

object TipMapper {
    fun toDomain(dto: TipDTO): TipDomain = TipDomain(id = dto.id, title = dto.title, text = dto.text, readMoreURL = dto.readMoreURL)

    fun toDTO(domain: TipDomain): TipDTO =
        TipDTO(id = domain.id, title = domain.title, text = domain.text, readMoreURL = domain.readMoreURL)
}
