package com.bellako.kiwi.features.personality.data

data class Personality(
    val realName: UserName,
    val knightName: UserName,
    val build: String,
) {
    fun toDTO(): PersonalityDTO =
        PersonalityDTO(
            realName = realName.value,
            knightName = knightName.value,
            build = build,
        )

    fun toState(): PersonalityState =
        PersonalityState(
            realName = realName.value,
            knightName = knightName.value,
            build = build,
        )
}
