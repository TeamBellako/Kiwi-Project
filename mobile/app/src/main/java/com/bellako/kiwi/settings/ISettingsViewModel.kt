package com.bellako.kiwi.settings

import com.bellako.kiwi.common.UIState
import kotlinx.coroutines.flow.StateFlow

interface ISettingsViewModel {
    val state: StateFlow<SettingsState?>
    val isLoading: StateFlow<Boolean>
    val uiState: StateFlow<UIState<Unit>>

    fun reset()
    fun loadSettings()
    fun updateSettings(state: SettingsState)
}
