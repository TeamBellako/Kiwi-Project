package com.bellako.kiwi.userSettings

import kotlinx.coroutines.flow.StateFlow

interface IUserSettingsViewModel {
    val state: StateFlow<UserSettingsState?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    fun loadSettings()
    fun updateSettings(state: UserSettingsState)

    fun clearToken()
}
