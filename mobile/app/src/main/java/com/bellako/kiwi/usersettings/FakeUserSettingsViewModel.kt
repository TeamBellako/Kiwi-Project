package com.bellako.kiwi.usersettings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeUserSettingsViewModel(private val backingState: UserSettingsState) : IUserSettingsViewModel {
    private val _state = MutableStateFlow(backingState)
    override val state: StateFlow<UserSettingsState?> get() = _state

    override val isLoading = MutableStateFlow(false)
    override val error = MutableStateFlow<String?>(null)

    override fun loadSettings() = Unit
    override fun updateSettings() = Unit
}