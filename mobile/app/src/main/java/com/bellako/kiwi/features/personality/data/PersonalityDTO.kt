package com.bellako.kiwi.features.personality.data

data class PersonalityDTO (
    val realName: String,
    val knightName: String,
    val build: String
){
    fun toState() : PersonalityState {
        return PersonalityState(
            realName = realName,
            knightName = knightName,
            build = build
        )
    }

    fun toDomainObject(): Result<Personality> {
        val realNameResult = UserName.of(realName)
        return realNameResult.fold(
            onSuccess = { validRealName ->
                val knightNameResult = UserName.of(knightName)
                knightNameResult.fold(
                    onSuccess = { validKnightName ->
                        Result.success(Personality(validRealName, validKnightName, build))
                    },
                    onFailure = { err -> Result.failure(err) }
                )
            },
            onFailure = { err -> Result.failure(err) }
        )
    }
}