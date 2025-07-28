package com.bellako.kiwi.features.personality.model

import com.bellako.kiwi.features.personality.data.PersonalityBuildDTO
import com.bellako.kiwi.features.personality.data.PersonalityDTO
import com.bellako.kiwi.features.personality.data.PersonalityUserNameDTO
import com.bellako.kiwi.common.model.HealthApiService

class PersonalityRepository(
    private val api: IPersonalityAPI,
    private val healthApi: HealthApiService
) {

    suspend fun getPersonality(): Result<PersonalityDTO> =
        runCatching { api.getPersonality() }

    suspend fun updateRealName(dto: PersonalityUserNameDTO): Result<Unit> =
        runCatching { api.updateRealName(dto) }

    suspend fun updateKnightName(dto: PersonalityUserNameDTO): Result<Unit> =
        runCatching { api.updateKnightName(dto) }

    suspend fun updateBuild(dto: PersonalityBuildDTO): Result<Unit> =
        runCatching { api.updateBuild(dto) }

}