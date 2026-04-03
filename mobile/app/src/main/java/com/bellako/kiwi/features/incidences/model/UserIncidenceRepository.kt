package com.bellako.kiwi.features.incidences.model

import com.bellako.kiwi.features.incidences.data.UserIncidenceDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserIncidenceRepository
    @Inject
    constructor(
        private val api: IUserIncidenceAPI,
    ) {
        suspend fun getUserIncidence(incidenceName: String): Boolean = api.getUserIncidence(incidenceName)

        suspend fun updateOrCreateUserIncidence(dto: UserIncidenceDTO) = api.updateOrCreateUserIncidence(dto)
    }
