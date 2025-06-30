package com.bellako.kiwi.features.settings

import com.bellako.kiwi.features.common.IBaseViewModel

interface ISettingsViewModel : IBaseViewModel<SettingsState> {
    fun loadSettings()
    fun updateSettings(state: SettingsState)

    fun reset()
}

