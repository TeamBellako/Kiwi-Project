package com.bellako.kiwi.usersettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val repository: UserSettingsRepository,
) : ViewModel(), IUserSettingsViewModel {
    private val _state = MutableStateFlow<UserSettingsState?>(null)
    override val state: StateFlow<UserSettingsState?> = _state.asStateFlow()
    private var prevUserSettingsDto: UserSettingsState? = null

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _pendingSave = MutableSharedFlow<UserSettingsDto>(extraBufferCapacity = 1)
    private var debounceJob: Job? = null

    init {
        observeDebouncedChanges()
    }

    override fun loadSettings() {
        viewModelScope.launch {
            val result = repository.getUserSettings()
            result.onSuccess {
                _state.value = UserSettingsState.fromDto(it)
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    override fun updateSettings(userSettingsDto: UserSettingsDto) {
        if (_state.value != null && prevUserSettingsDto == _state.value) return

        prevUserSettingsDto = _state.value
        _state.value = userSettingsDto.toState()

        _state.value?.toDto()?.let {
            _pendingSave.tryEmit(it)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeDebouncedChanges() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            _pendingSave
                .debounce(500)
                .collectLatest { dto ->
                    saveSettings(dto)
                }
        }
    }

    private suspend fun saveSettings(dto: UserSettingsDto) {
        withContext(Dispatchers.IO) {
            repository.updateUserSettings(dto)
        }
    }
}
