package com.bellako.kiwi.features.personality

import com.bellako.kiwi.features.common.BaseFakeViewModel
import com.bellako.kiwi.services.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PersonalityFakeViewModel(
    initialState: PersonalityState,
) : BaseFakeViewModel(), IPersonalityViewModel {

    private val _state = MutableStateFlow<PersonalityState?>(initialState)
    override val state: StateFlow<PersonalityState?> = _state.asStateFlow()

    override fun reset() {
    }

    override fun loadPersonality() {
        handleSuccess()
    }

    override fun checkValid(state: PersonalityState): Result<Personality> {
        return state.toDomainObject().fold(
            onSuccess = { validState ->
                Result.success(Personality(validState.realName, validState.knightName))
            },
            onFailure = { err ->
                _uiState.value = UIState.Error(err.message.orEmpty())
                Result.failure(err)
            }
        )
    }

    override fun updateRealName(state: PersonalityState) {
        handleSuccess()
    }

    override fun updateKnightName(state: PersonalityState) {
        handleSuccess()
    }

    override fun onRealNameChanged(name: String) {
        _state.value = _state.value?.copy(realName = name)
    }

    override fun onKnightNameChanged(name: String) {
        _state.value = _state.value?.copy(knightName = name)
    }
}
