package com.bellako.kiwi.features.personality

import com.bellako.kiwi.services.network.HealthApiService

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