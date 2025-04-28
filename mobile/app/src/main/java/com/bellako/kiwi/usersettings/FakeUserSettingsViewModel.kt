package com.bellako.kiwi.usersettings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

class FakeUserSettingsViewModel(
    private var backingState: UserSettingsState
) : IUserSettingsViewModel {
    private val _state = MutableStateFlow(backingState)
    override val state: StateFlow<UserSettingsState?> = _state

    override var isLoading = MutableStateFlow(false)
    override val error = MutableStateFlow<String?>(null)

    var simulateLoadError: Boolean = false
    var simulateUpdateError: Boolean = false
    var simulatedErrorMessage: String = "Something went wrong"

    override fun loadSettings() {
        isLoading.value = true
        if (simulateLoadError) {
            error.value = simulatedErrorMessage
        } else {
            isLoading.value = false
        }
    }

    override fun updateSettings(userSettingsDto: UserSettingsDto) {
        if (simulateUpdateError || simulateLoadError) {
            isLoading.value = simulateLoadError
            error.value = simulatedErrorMessage
        } else {
            _state.value = UserSettingsState.fromDto(userSettingsDto)
            error.value = null
        }
    }
}