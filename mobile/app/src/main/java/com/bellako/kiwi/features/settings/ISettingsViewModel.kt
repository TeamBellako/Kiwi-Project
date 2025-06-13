package com.bellako.kiwi.features.settings

import com.bellako.kiwi.services.common.UIState
import kotlinx.coroutines.flow.StateFlow

interface ISettingsViewModel {
    val state: StateFlow<SettingsState?>
    val isLoading: StateFlow<Boolean>
    val uiState: StateFlow<UIState<Unit>>

    fun reset()
    fun loadSettings()
    fun updateSettings(state: SettingsState)
}
