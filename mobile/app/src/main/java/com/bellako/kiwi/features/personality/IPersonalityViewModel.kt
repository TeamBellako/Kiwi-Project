package com.bellako.kiwi.features.personality

import com.bellako.kiwi.features.common.IBaseViewModel

interface IPersonalityViewModel : IBaseViewModel<PersonalityState> {

    fun reset()

    suspend fun loadPersonality(): Result<Unit>

    fun checkValid(): Result<Personality>

    suspend fun updateRealName(): Result<Unit>
    suspend fun updateKnightName(): Result<Unit>

    fun onRealNameChanged(name: String)
    fun onKnightNameChanged(name: String)

    suspend fun updateBuild(): Result<Unit>
}
