package com.bellako.kiwi.userSettings.viewModel

import com.bellako.kiwi.userSettings.types.UserSettings
import com.bellako.kiwi.userSettings.types.UserSettingsFactory
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.userSettings.types.UserSettingsValidationState
import com.bellako.kiwi.userSettings.types.ValidatedEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserSettingsFakeViewModel(
    private var backingState: UserSettingsState
) : IUserSettingsViewModel {

    private val _state = MutableStateFlow(backingState)
    override val state: StateFlow<UserSettingsState?> = _state

    override var isLoading = MutableStateFlow(false)

    private val _validationState = MutableStateFlow(UserSettingsValidationState())
    override val validationState: StateFlow<UserSettingsValidationState> = _validationState

    private var currentDomainSettings: UserSettings? = UserSettingsFactory.fromState(backingState).getOrNull()

    var simulateLoadError: Boolean = false
    var simulateUpdateError: Boolean = false
    var simulatedErrorMessage: String = "Something went wrong"

    override fun loadSettings() {
        isLoading.value = true

        if (simulateLoadError) {
            _validationState.value = UserSettingsValidationState(generalError = simulatedErrorMessage)
        } else {
            _validationState.value = UserSettingsValidationState()
        }

        isLoading.value = false
    }

    override fun updateSettings(state: UserSettingsState) {
        _state.value = state

        if (!ValidatedEmail.isValid(state.email)) {
            _validationState.value = UserSettingsValidationState(emailError = "Invalid email format")
            return
        }

        _validationState.value = UserSettingsValidationState()

        if (simulateUpdateError) {
            _validationState.value = UserSettingsValidationState(generalError = simulatedErrorMessage)
            return
        }

        val result = UserSettingsFactory.fromState(state)

        result.onFailure {
            _validationState.value = UserSettingsValidationState(emailError = it.message)
        }.onSuccess { domain ->
            if (currentDomainSettings != domain) {
                currentDomainSettings = domain
                _state.value = UserSettingsFactory.toState(domain)
                _validationState.value = UserSettingsValidationState() // Clear errors after success
            }
        }
    }
}
