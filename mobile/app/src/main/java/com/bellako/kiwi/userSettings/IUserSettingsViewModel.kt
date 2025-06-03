package com.bellako.kiwi.userSettings

import com.bellako.kiwi.common.UIState
import kotlinx.coroutines.flow.StateFlow

interface IUserSettingsViewModel {
    val state: StateFlow<UserSettingsState?>
    val isLoading: StateFlow<Boolean>
    val uiState: StateFlow<UIState<Unit>>

    fun reset()
    fun loadSettings()
    fun updateSettings(state: UserSettingsState)
    fun clearToken()
}
