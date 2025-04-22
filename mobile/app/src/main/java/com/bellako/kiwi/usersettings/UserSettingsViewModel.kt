package com.bellako.kiwi.usersettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserSettingsViewModel(private val repository: UserSettingsRepository) : ViewModel() {
    private val _state = MutableStateFlow<UserSettingsState?>(null)
    val state: StateFlow<UserSettingsState?> = _state

    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun loadSettings() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val result = repository.getUserSettings()
            if (result.isSuccess) {
                _state.value = result.getOrNull()?.toState()
            } else {
                error.value = result.exceptionOrNull()?.message
            }

            isLoading.value = false
        }
    }

    fun updateSettings() {
        viewModelScope.launch {
            val currentState = _state.value ?: return@launch
            repository.updateUserSettings(currentState.toDto())
        }
    }
}