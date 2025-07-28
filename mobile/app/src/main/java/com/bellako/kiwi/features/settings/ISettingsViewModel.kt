package com.bellako.kiwi.features.settings

import com.bellako.kiwi.features.common.IBaseViewModel

interface ISettingsViewModel : IBaseViewModel<SettingsState> {
    suspend fun loadSettings()
    suspend fun updateSettings(state: SettingsState)
}

