package com.bellako.kiwi.features.personality

import com.bellako.kiwi.types.BERSERKER

object PersonalityTestFactory {

    fun validPersonalityDTO(): PersonalityDTO =
        PersonalityDTO(
            "Finn",
            "Human",
            BERSERKER
        )

    fun validPersonalityRealNameDTO(): PersonalityUserNameDTO =
        PersonalityUserNameDTO(
            "Finn"
        )

    fun validPersonalityKnightNameDTO(): PersonalityUserNameDTO =
        PersonalityUserNameDTO(
            "Human"
        )

    fun validPersonalityBuildDTO(): PersonalityBuildDTO =
        PersonalityBuildDTO(
            BERSERKER
        )

}
