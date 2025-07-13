package com.bellako.kiwi.features.personality

import com.bellako.kiwi.types.UserName

data class PersonalityState (
    val realName: String,
    val knightName: String
){
    fun toDTO() : PersonalityDTO {
        return PersonalityDTO(
            realName = realName,
            knightName = knightName
        )
    }

    fun toDomainObject(): Result<Personality> {
        val realNameResult = UserName.of(realName)
        return realNameResult.fold(
            onSuccess = { validRealName ->
                val knightNameResult = UserName.of(knightName)
                knightNameResult.fold(
                    onSuccess = { validKnightName ->
                        Result.success(Personality(validRealName, validKnightName))
                    },
                    onFailure = { err -> Result.failure(err) }
                )
            },
            onFailure = { err -> Result.failure(err) }
        )
    }
}