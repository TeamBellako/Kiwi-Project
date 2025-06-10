package com.bellako.kiwi.userSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.UIState
import com.bellako.kiwi.network.AuthRepository
import com.bellako.kiwi.utils.HTTPUtils.mapExceptionToUIState
import com.bellako.kiwi.utils.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val repository: UserSettingsRepository,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel(), IUserSettingsViewModel {

    private val _state = MutableStateFlow<UserSettingsState?>(null)
    override val state: StateFlow<UserSettingsState?> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    private var previousValidDomainSettings: UserSettings? = null
    private var previousDomainSettings: UserSettings? = null
    private val _pendingSave = MutableSharedFlow<UserSettings>(extraBufferCapacity = 1)
    private var debounceJob: Job? = null

    init {
        observeDebouncedChanges()
    }

    override fun reset() {
        previousValidDomainSettings = null
    }

    override fun loadSettings() {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        viewModelScope.launch {
            val result = repository.getUserSettings()
            result.onSuccess { dto ->
                dto.toDomainObject()
                    .onSuccess { domain ->
                        _state.value = domain.toState()
                        previousDomainSettings = domain
                        _uiState.value = UIState.Success(Unit)
                    }
                    .onFailure { ex ->
                        _uiState.value = mapExceptionToUIState(ex)
                    }
            }.onFailure { ex ->
                _uiState.value = mapExceptionToUIState(ex)
            }
            _isLoading.value = false
        }
    }

    override fun updateSettings(state: UserSettingsState) {
        _state.value = state

        state.toDomainObject().onSuccess { domain ->
            if (previousValidDomainSettings == domain) return

            Logger.info("Queueing settings save")
            previousValidDomainSettings = domain
            _pendingSave.tryEmit(domain)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeDebouncedChanges() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            _pendingSave
                .debounce(500)
                .collectLatest { domain ->
                    saveSettings(domain)
                }
        }
    }

    private suspend fun saveSettings(domain: UserSettings) {
        withContext(dispatcher) {
            repository.pingServer()
                .onSuccess {
                    Logger.info("Saving user settings")

                    repository.updateUserSettings(domain.toDTO())
                        .onSuccess {
                            _uiState.value = UIState.Success(Unit)
                        }
                        .onFailure { throwable ->
                            _uiState.value = mapExceptionToUIState(throwable)
                        }
                }
                .onFailure { throwable ->
                    _uiState.value = mapExceptionToUIState(throwable)
                }
        }
    }

}
