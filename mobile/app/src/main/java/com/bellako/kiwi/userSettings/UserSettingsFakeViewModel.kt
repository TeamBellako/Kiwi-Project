package com.bellako.kiwi.userSettings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSettingsFakeViewModel(
    private var backingState: UserSettingsState
) : IUserSettingsViewModel {

    private val _state = MutableStateFlow(backingState)
    override val state: StateFlow<UserSettingsState?> = _state

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    private var currentDomainSettings: UserSettings? = backingState.toDomainObject().getOrNull()

    var simulateLoadError: Boolean = false
    var simulateUpdateError: Boolean = false
    var simulatedErrorMessage: String = "Something went wrong"

    override fun loadSettings() {
        _isLoading.value = true

        if (simulateLoadError) {
            _error.value = simulatedErrorMessage
        } else {
            _error.value = null
        }

        _isLoading.value = false
    }

    override fun updateSettings(state: UserSettingsState) {
        _state.value = state

        _error.value = null

        if (simulateUpdateError) {
            _error.value = simulatedErrorMessage
            return
        }

        val result = state.toDomainObject()

        result
            .onFailure {
                _error.value = simulatedErrorMessage
            }
            .onSuccess { domain ->
                if (currentDomainSettings != domain) {
                    currentDomainSettings = domain
                    _state.value = domain.toState()
                    _error.value = null
                }
            }
    }

    override fun clearToken() {}
}
