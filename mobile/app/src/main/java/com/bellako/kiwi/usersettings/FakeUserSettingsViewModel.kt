package com.bellako.kiwi.usersettings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeUserSettingsViewModel(
    private var backingState: UserSettingsState
) : IUserSettingsViewModel {
    private val _state = MutableStateFlow(backingState)
    override val state: StateFlow<UserSettingsState?> = _state

    override val isLoading = MutableStateFlow(false)
    override val error = MutableStateFlow<String?>(null)

    var simulateError: Boolean = false
    var simulatedErrorMessage: String = "Something went wrong"

    override fun loadSettings() = Unit

    override fun updateSettings(userSettingsDto: UserSettingsDto) {
        if (simulateError) {
            error.value = simulatedErrorMessage
        } else {
            _state.value = UserSettingsState.fromDto(userSettingsDto)
            error.value = null
        }
    }
}