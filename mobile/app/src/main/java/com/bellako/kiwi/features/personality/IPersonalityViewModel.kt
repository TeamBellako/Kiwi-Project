package com.bellako.kiwi.features.personality

import com.bellako.kiwi.features.common.IBaseViewModel

interface IPersonalityViewModel : IBaseViewModel<PersonalityState> {

    fun reset()

    fun loadPersonality()

    fun checkValid(state: PersonalityState): Result<Personality>

    fun updateRealName(state: PersonalityState)
    fun updateKnightName(state: PersonalityState)

    fun onRealNameChanged(name: String)
    fun onKnightNameChanged(name: String)

    suspend fun updateBuild(state: PersonalityState): Result<Unit>
}
