package com.bellako.kiwi.features.incidences.model

import com.bellako.kiwi.features.incidences.data.UserIncidenceDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserIncidenceManager
    @Inject
    constructor(
        private val repository: UserIncidenceRepository,
    ) {
        suspend fun getIncidence(name: String): Boolean = repository.getUserIncidence(name)

        suspend fun setIncidence(
            name: String,
            value: Boolean,
        ) {
            repository.updateOrCreateUserIncidence(
                UserIncidenceDTO(
                    name = name,
                    value = value,
                ),
            )
        }
    }
