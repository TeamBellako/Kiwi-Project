package com.bellako.kiwi.features.personality

import com.bellako.kiwi.types.UserName

data class Personality (
    val realName: UserName,
    val knightName: UserName
){
    fun toDTO() : PersonalityDTO {
        return PersonalityDTO(
            realName = realName.value,
            knightName = knightName.value
        )
    }

    fun toState() : PersonalityState {
        return PersonalityState(
            realName = realName.value,
            knightName = knightName.value
        )
    }
}