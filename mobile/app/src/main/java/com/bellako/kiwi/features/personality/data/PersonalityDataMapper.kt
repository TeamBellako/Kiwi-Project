package com.bellako.kiwi.features.personality.data

import android.os.Build
import androidx.annotation.RequiresApi

object PersonalityDataMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: PersonalityDTO): Result<PersonalityDomain> {
        val realNameResult = UserName.of(dto.realName)
        return realNameResult.fold(
            onSuccess = { validRealName ->
                val knightNameResult = UserName.of(dto.knightName)
                knightNameResult.fold(
                    onSuccess = { validKnightName ->
                        Result.success(
                            PersonalityDomain(
                                realName = validRealName,
                                knightName = validKnightName,
                                build = dto.build,
                                goodApps = dto.goodApps,
                                badApps = dto.badApps,
                            ),
                        )
                    },
                    onFailure = { err -> Result.failure(err) },
                )
            },
            onFailure = { err -> Result.failure(err) },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(state: PersonalityState): Result<PersonalityDomain> {
        val realNameResult = UserName.of(state.realName)
        return realNameResult.fold(
            onSuccess = { validRealName ->
                val knightNameResult = UserName.of(state.knightName)
                knightNameResult.fold(
                    onSuccess = { validKnightName ->
                        Result.success(
                            PersonalityDomain(
                                realName = validRealName,
                                knightName = validKnightName,
                                build = state.build,
                                goodApps = state.goodApps,
                                badApps = state.badApps,
                            ),
                        )
                    },
                    onFailure = { err -> Result.failure(err) },
                )
            },
            onFailure = { err -> Result.failure(err) },
        )
    }

    fun toState(domain: PersonalityDomain): PersonalityState =
        PersonalityState(
            realName = domain.realName.value,
            knightName = domain.knightName.value,
            build = domain.build,
            goodApps = domain.goodApps,
            badApps = domain.badApps,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toState(dto: PersonalityDTO): Result<PersonalityState> {
        val domain = toDomain(dto)
        return domain.fold(
            onSuccess = { validDomain ->
                Result.success(toState(validDomain))
            },
            onFailure = { err -> Result.failure(err) },
        )
    }

    fun toDTO(domain: PersonalityDomain): PersonalityDTO =
        PersonalityDTO(
            realName = domain.realName.value,
            knightName = domain.knightName.value,
            build = domain.build,
            goodApps = domain.goodApps,
            badApps = domain.badApps,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDTO(state: PersonalityState): Result<PersonalityDTO> {
        val domain = toDomain(state)
        return domain.fold(
            onSuccess = { validDomain ->
                Result.success(toDTO(validDomain))
            },
            onFailure = { err -> Result.failure(err) },
        )
    }
}
