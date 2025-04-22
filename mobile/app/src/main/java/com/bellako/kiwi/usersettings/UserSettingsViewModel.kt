package com.bellako.kiwi.usersettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val repository: UserSettingsRepository
) : ViewModel(), IUserSettingsViewModel {
    private val _state = MutableStateFlow<UserSettingsState?>(null)
    override val state: StateFlow<UserSettingsState?> = _state

    override val isLoading = MutableStateFlow(false)
    override val error = MutableStateFlow<String?>(null)

    override fun loadSettings() {
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

    override fun updateSettings() {
        viewModelScope.launch {
            val currentState = _state.value ?: return@launch
            repository.updateUserSettings(currentState.toDto())
        }
    }
}