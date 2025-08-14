package com.bellako.kiwi.features.personality.tests

import com.bellako.kiwi.features.personality.data.*
import com.bellako.kiwi.features.personality.data.PersonalityBuildDTO
import com.bellako.kiwi.features.personality.data.PersonalityDTO
import com.bellako.kiwi.features.personality.data.PersonalityUserNameDTO

object PersonalityTestFactory {

    fun validPersonalityDTO(): PersonalityDTO =
        PersonalityDTO(
            "Finn",
            "Human",
            BERSERKER,
            listOf(),
            listOf()
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
