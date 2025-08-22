package com.bellako.kiwi.features.personality.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.personality.data.Personality
import com.bellako.kiwi.features.personality.data.PersonalityState

interface IPersonalityViewModel : IBaseViewModel<PersonalityState> {
    suspend fun loadPersonality(): Result<Unit>

    fun checkValid(): Result<Personality>

    suspend fun updateRealName(): Result<Unit>

    suspend fun updateKnightName(): Result<Unit>

    fun onRealNameChanged(name: String)

    fun onKnightNameChanged(name: String)

    suspend fun updateBuild(): Result<Unit>

    fun onAppsChanged(goodApps: List<String>, badApps: List<String>)
    suspend fun updateApps(): Result<Unit>
}
