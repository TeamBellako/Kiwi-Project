package com.bellako.kiwi.features.personality.data

data class PersonalityDTO(
    val realName: String,
    val knightName: String,
    val build: String,
    val goodApps: List<String>,
    val badApps: List<String>,
) {
    fun toState(): PersonalityState =
        PersonalityState(
            realName = realName,
            knightName = knightName,
            build = build,
            goodApps = goodApps,
            badApps = badApps,
        )

    fun toDomainObject(): Result<Personality> {
        val realNameResult = UserName.of(realName)
        return realNameResult.fold(
            onSuccess = { validRealName ->
                val knightNameResult = UserName.of(knightName)
                knightNameResult.fold(
                    onSuccess = { validKnightName ->
                        Result.success(Personality(validRealName, validKnightName, build, goodApps, badApps))
                    },
                    onFailure = { err -> Result.failure(err) },
                )
            },
            onFailure = { err -> Result.failure(err) },
        )
    }
}
