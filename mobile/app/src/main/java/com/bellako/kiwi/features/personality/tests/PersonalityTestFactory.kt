package com.bellako.kiwi.features.personality.tests

import com.bellako.kiwi.features.personality.data.*
import com.bellako.kiwi.features.personality.data.PersonalityBuildDTO
import com.bellako.kiwi.features.personality.data.PersonalityDTO
import com.bellako.kiwi.features.personality.data.PersonalityUserNameDTO

object PersonalityTestFactory {
    fun validPersonalityDTO(): PersonalityDTO =
        PersonalityDTO(
            validPersonalityRealNameDTO().name,
            validPersonalityKnightNameDTO().name,
            validPersonalityBuildDTO().build,
            validPersonalityAppsDTO().goodApps,
            validPersonalityAppsDTO().badApps,
        )

    fun validPersonalityRealNameDTO(): PersonalityUserNameDTO = PersonalityUserNameDTO("Finn")

    fun validPersonalityKnightNameDTO(): PersonalityUserNameDTO = PersonalityUserNameDTO("Human")

    fun validPersonalityBuildDTO(): PersonalityBuildDTO = PersonalityBuildDTO(BERSERKER)

    fun validPersonalityAppsDTO(): PersonalityAppsDTO = PersonalityAppsDTO(listOf("GrowTale"), listOf("X"))
}
