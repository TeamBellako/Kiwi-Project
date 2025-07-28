package com.bellako.kiwi.features.settings.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.settings.data.SettingsState

interface ISettingsViewModel : IBaseViewModel<SettingsState> {
    suspend fun loadSettings()
    suspend fun updateSettings(state: SettingsState)
}

