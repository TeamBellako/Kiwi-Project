package com.bellako.kiwi.features.personality.tests

import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.data.Personality
import com.bellako.kiwi.common.data.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PersonalityFakeViewModel(
    initialState: PersonalityState,
) : BaseFakeViewModel(), IPersonalityViewModel {

    private val _state = MutableStateFlow<PersonalityState?>(initialState)
    override val state: StateFlow<PersonalityState?> = _state.asStateFlow()

    override suspend fun loadPersonality(): Result<Unit> {
        return Result.success(Unit)
    }

    override fun checkValid(): Result<Personality> {
        return _state.value?.toDomainObject()?.fold(
            onSuccess = { validState ->
                Result.success(Personality(validState.realName, validState.knightName, validState.build, validState.goodApps, validState.badApps))
            },
            onFailure = { err ->
                _uiState.value = UIState.Error(err.message.orEmpty())
                Result.failure(err)
            }
        ) ?: Result.failure(Exception("Invalid state"))
    }

    override suspend fun updateRealName(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateKnightName(): Result<Unit> {
        return Result.success(Unit)
    }

    override fun onRealNameChanged(name: String) {
        _state.value = _state.value?.copy(realName = name)
    }

    override fun onKnightNameChanged(name: String) {
        _state.value = _state.value?.copy(knightName = name)
    }

    override suspend fun updateBuild(): Result<Unit> {
        return Result.success(Unit)
    }

    override fun onAppsChanged(goodApps: List<String>, badApps: List<String>) {
        _state.value = _state.value?.copy(goodApps = goodApps)
        _state.value = _state.value?.copy(badApps = badApps)
    }

    override suspend fun updateApps(): Result<Unit> {
        return Result.success(Unit)
    }
}
