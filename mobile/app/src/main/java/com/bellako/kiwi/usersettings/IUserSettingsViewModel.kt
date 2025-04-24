package com.bellako.kiwi.usersettings

import kotlinx.coroutines.flow.StateFlow

interface IUserSettingsViewModel {
    val state: StateFlow<UserSettingsState?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    fun loadSettings()
    fun updateSettings(userSettingsDto: UserSettingsDto)
}
