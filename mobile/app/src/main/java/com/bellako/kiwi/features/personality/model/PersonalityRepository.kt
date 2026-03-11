package com.bellako.kiwi.features.personality.model

import com.bellako.kiwi.features.personality.data.PersonalityAppsDTO
import com.bellako.kiwi.features.personality.data.PersonalityBuildDTO
import com.bellako.kiwi.features.personality.data.PersonalityDTO
import com.bellako.kiwi.features.personality.data.PersonalityUserNameDTO
import jakarta.inject.Inject

class PersonalityRepository
    @Inject
    constructor(
        private val api: IPersonalityAPI,
    ) : IPersonalityRepository {
        override suspend fun getPersonality(): Result<PersonalityDTO> = runCatching { api.getPersonality() }

        override suspend fun updateRealName(dto: PersonalityUserNameDTO): Result<Unit> = runCatching { api.updateRealName(dto) }

        override suspend fun updateKnightName(dto: PersonalityUserNameDTO): Result<Unit> = runCatching { api.updateKnightName(dto) }

        override suspend fun updateBuild(dto: PersonalityBuildDTO): Result<Unit> = runCatching { api.updateBuild(dto) }

        override suspend fun updateApps(dto: PersonalityAppsDTO): Result<Unit> = runCatching { api.updateApps(dto) }
    }
