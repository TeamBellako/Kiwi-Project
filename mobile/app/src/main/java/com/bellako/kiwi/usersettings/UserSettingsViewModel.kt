package com.bellako.kiwi.usersettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
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
    private val dispatcher: CoroutineDispatcher
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
        withContext(dispatcher) {
            repository.updateUserSettings(dto)
                .onFailure { throwable ->
                    val errorMessage = when (throwable) {
                        is HttpException -> {
                            val errorBody = throwable.response()?.errorBody()?.string()
                            parseErrorMessage(errorBody)
                        }
                        else -> throwable.message ?: "Unknown error"
                    }
                    _error.value = errorMessage
                }
        }
    }

    fun parseErrorMessage(json: String?): String {
        return try {
            val jsonObject = JSONObject(json ?: "")
            jsonObject.getString("message")
        } catch (e: Exception) {
            "Something went wrong"
        }
    }
}
