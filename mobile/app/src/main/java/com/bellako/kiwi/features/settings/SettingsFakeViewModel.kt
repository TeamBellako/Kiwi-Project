package com.bellako.kiwi.features.settings

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.features.common.BaseFakeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsFakeViewModel(
    backingState: SettingsState
) : BaseFakeViewModel(), ISettingsViewModel {

    private val _state = MutableStateFlow<SettingsState?>(backingState)
    override val state: StateFlow<SettingsState?> = _state.asStateFlow()

    private var currentDomainSettings: Settings? = backingState.toDomainObject().getOrNull()

    var simulateLoadError: Boolean = false
    var simulateUpdateError: Boolean = false
    var simulatedException: Exception = Exception("Something went wrong")

    override fun reset() {}

    override fun loadSettings() {
        setLoading(true)

        // Simulate an error or successful loading asynchronously
        viewModelScope.launch {
            if (simulateLoadError) {
                handleError(simulatedException)
            } else {
                handleSuccess()
            }

            setLoading(false)
        }
    }

    override fun updateSettings(state: SettingsState) {
        setLoading(true)

        // Simulate an error or successful update asynchronously
        viewModelScope.launch {
            if (simulateUpdateError) {
                handleError(simulatedException)
                setLoading(false)
                return@launch
            }

            val result = state.toDomainObject()

            result.onFailure {
                handleError(simulatedException)
            }.onSuccess { domain ->
                if (currentDomainSettings != domain) {
                    currentDomainSettings = domain
                    _state.value = domain.toState()
                    handleSuccess()
                }
            }

            setLoading(false)
        }
    }
}

