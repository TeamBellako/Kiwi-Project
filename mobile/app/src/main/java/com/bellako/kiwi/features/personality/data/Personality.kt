package com.bellako.kiwi.features.personality.data

data class Personality(
    val realName: UserName,
    val knightName: UserName,
    val build: String,
    val goodApps: List<String>,
    val badApps: List<String>,
) {
    fun toDTO(): PersonalityDTO =
        PersonalityDTO(
            realName = realName.value,
            knightName = knightName.value,
            build = build,
            goodApps = goodApps,
            badApps = badApps,
        )

    fun toState(): PersonalityState =
        PersonalityState(
            realName = realName.value,
            knightName = knightName.value,
            build = build,
            goodApps = goodApps,
            badApps = badApps,
        )
}
