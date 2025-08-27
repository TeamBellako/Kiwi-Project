package com.bellako.kiwi.features.personality.data

import android.os.Build
import androidx.annotation.RequiresApi

object PersonalityDataMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: PersonalityDTO): PersonalityDomain =
        PersonalityDomain(
            realName = UserName.of(dto.realName).getOrNull(),
            knightName = UserName.of(dto.knightName).getOrNull(),
            build = dto.build,
            goodApps = dto.goodApps,
            badApps = dto.badApps,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(state: PersonalityState): PersonalityDomain =
        PersonalityDomain(
            realName = UserName.of(state.realName).getOrNull(),
            knightName = UserName.of(state.knightName).getOrNull(),
            build = state.build,
            goodApps = state.goodApps,
            badApps = state.badApps,
        )

    fun toState(domain: PersonalityDomain): PersonalityState =
        PersonalityState(
            realName = domain.realName?.value ?: "",
            knightName = domain.knightName?.value ?: "",
            build = domain.build,
            goodApps = domain.goodApps,
            badApps = domain.badApps,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toState(dto: PersonalityDTO): PersonalityState = toState(toDomain(dto))

    fun toDTO(domain: PersonalityDomain): PersonalityDTO =
        PersonalityDTO(
            realName = domain.realName?.value ?: "",
            knightName = domain.knightName?.value ?: "",
            build = domain.build,
            goodApps = domain.goodApps,
            badApps = domain.badApps,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDTO(state: PersonalityState): PersonalityDTO = toDTO(toDomain(state))
}
