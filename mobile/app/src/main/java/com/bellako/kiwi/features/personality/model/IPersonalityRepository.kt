package com.bellako.kiwi.features.personality.model

import com.bellako.kiwi.features.personality.data.PersonalityAppsDTO
import com.bellako.kiwi.features.personality.data.PersonalityBuildDTO
import com.bellako.kiwi.features.personality.data.PersonalityDTO
import com.bellako.kiwi.features.personality.data.PersonalityUserNameDTO

interface IPersonalityRepository {
    suspend fun getPersonality(): Result<PersonalityDTO>

    suspend fun updateRealName(dto: PersonalityUserNameDTO): Result<Unit>

    suspend fun updateKnightName(dto: PersonalityUserNameDTO): Result<Unit>

    suspend fun updateBuild(dto: PersonalityBuildDTO): Result<Unit>

    suspend fun updateApps(dto: PersonalityAppsDTO): Result<Unit>
}
