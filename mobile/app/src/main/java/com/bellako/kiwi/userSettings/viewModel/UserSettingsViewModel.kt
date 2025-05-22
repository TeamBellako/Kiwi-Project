package com.bellako.kiwi.userSettings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.network.AuthRepository
import com.bellako.kiwi.userSettings.network.UserSettingsRepository
import com.bellako.kiwi.userSettings.types.UserSettings
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.utils.Logger
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
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
    private val authRepository: AuthRepository,
) : ViewModel(), IUserSettingsViewModel {

    private val _state = MutableStateFlow<UserSettingsState?>(null)
    override val state: StateFlow<UserSettingsState?> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    private var previousValidDomainSettings: UserSettings? = null

    private var previousDomainSettings: UserSettings? = null
    private val _pendingSave = MutableSharedFlow<UserSettings>(extraBufferCapacity = 1)
    private var debounceJob: Job? = null

    init {
        observeDebouncedChanges()
    }

    override fun loadSettings() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getUserSettings()
            result.onSuccess { dto ->
                dto.toDomainObject()
                    .onSuccess { domain ->
                        _state.value = domain.toState()
                        previousDomainSettings = domain
                        _isLoading.value = false
                        _error.value = null
                    }
                    .onFailure { ex ->
                        if (ex is HttpException && ex.code() != 500) {
                            _error.value = ex.message.toString()
                        } else {
                            _error.value = "An unexpected error occurred."
                        }
                        _isLoading.value = false
                    }
            }.onFailure { ex ->
                if (ex is HttpException && ex.code() != 500) {
                    _error.value = ex.message.toString()
                } else {
                    _error.value = "An unexpected error occurred."
                }
                _isLoading.value = false
            }
        }
    }


    override fun updateSettings(state: UserSettingsState) {
        _state.value = state
        _error.value = null

        state.toDomainObject().onSuccess { domain ->
            if (previousValidDomainSettings == domain) return

            Logger.info("Queueing settings save")

            previousValidDomainSettings = domain
            _pendingSave.tryEmit(domain)
        }
    }

    override fun clearToken() {
        authRepository.setJwtToken("")
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
                            _error.value = null
                        }
                        .onFailure { throwable ->
                            val errorMessage = when (throwable) {
                                is HttpException -> {
                                    val errorBody = throwable.response()?.errorBody()?.string()
                                    parseErrorMessage(errorBody)
                                }
                                else -> "Unknown error"
                            }
                            _error.value = errorMessage
                        }
                }
                .onFailure {
                    loadSettings()
                }
        }
    }

    data class ErrorResponse(val message: String?)

    fun parseErrorMessage(json: String?): String {
        if (json.isNullOrBlank()) return "Unknown error"
        return try {
            val errorResponse = Gson().fromJson(json, ErrorResponse::class.java)
            errorResponse.message ?: "Unknown error"
        } catch (e: Exception) {
            "Error parsing message: ${e.localizedMessage}"
        }
    }
}
