package com.bellako.kiwi.features.personality.tests

import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.data.UserName
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PersonalityFakeViewModel(
    initialState: PersonalityState,
) : BaseFakeViewModel(),
    IPersonalityViewModel {
    private val _state = MutableStateFlow<PersonalityState?>(initialState)
    override val state: StateFlow<PersonalityState?> = _state.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    // ---------------------------------------------------------------------------------------------

    override suspend fun loadPersonality(): Result<Unit> = Result.success(Unit)

    // ---------------------------------------------------------------------------------------------

    override fun onRealNameChanged(name: String) {
        _state.value = _state.value?.copy(realName = name)
    }

    override fun onKnightNameChanged(name: String) {
        _state.value = _state.value?.copy(knightName = name)
    }

    override fun checkRealNameValid(): Boolean = checkNameValid(_state.value!!.realName)

    override fun checkKnightNameValid(): Boolean = checkNameValid(_state.value!!.knightName)

    private fun checkNameValid(name: String): Boolean =
        UserName.of(name).fold(
            onSuccess = { _ -> true },
            onFailure = { err ->
                setUiState(UIState.Error(err.message.orEmpty()))
                false
            },
        )

    override suspend fun updateRealName(): Result<Unit> = getTestResult()

    override suspend fun updateKnightName(): Result<Unit> = getTestResult()

    // ---------------------------------------------------------------------------------------------

    override suspend fun updateBuild(): Result<Unit> = getTestResult()

    // ---------------------------------------------------------------------------------------------

    override fun onAppsChanged(
        goodApps: List<String>,
        badApps: List<String>,
        neutralApps: List<String>,
    ) {
        _state.value = _state.value?.copy(goodApps = goodApps)
        _state.value = _state.value?.copy(badApps = badApps)
        _state.value = _state.value?.copy(neutralApps = neutralApps)
    }

    override suspend fun updateApps(): Result<Unit> = getTestResult()

    fun getTestResult(): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }
}
