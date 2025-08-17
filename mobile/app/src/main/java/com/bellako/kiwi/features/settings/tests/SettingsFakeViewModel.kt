package com.bellako.kiwi.features.settings.tests

import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.settings.data.Settings
import com.bellako.kiwi.features.settings.data.SettingsState
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsFakeViewModel(
    backingState: SettingsState,
) : BaseFakeViewModel(),
    ISettingsViewModel {
    private val _state = MutableStateFlow<SettingsState?>(backingState)
    override val state: StateFlow<SettingsState?> = _state.asStateFlow()

    private var currentDomainSettings: Settings? = backingState.toDomainObject().getOrNull()

    var simulateLoadError: Boolean = false
    var simulateUpdateError: Boolean = false
    var simulatedException: Exception = Exception("Something went wrong")

    override suspend fun loadSettings() {
        setLoading(true)

        // Simulate an error or successful loading asynchronously
        if (simulateLoadError) {
            handleError(simulatedException)
        } else {
            handleSuccess()
        }

        setLoading(false)
    }

    override suspend fun updateSettings(state: SettingsState) {
        setLoading(true)

        // Simulate an error or successful update asynchronously
        if (simulateUpdateError) {
            handleError(simulatedException)
            setLoading(false)
            return
        }

        val result = state.toDomainObject()

        result
            .onFailure {
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
