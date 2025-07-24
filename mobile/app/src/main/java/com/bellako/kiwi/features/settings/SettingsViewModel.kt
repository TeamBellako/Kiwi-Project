package com.bellako.kiwi.features.settings

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.services.common.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.collectLatest
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.services.common.BaseViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@OptIn(FlowPreview::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : BaseViewModel(), ISettingsViewModel {

    private val _state = MutableStateFlow<SettingsState?>(null)
    override val state: StateFlow<SettingsState?> = _state.asStateFlow()

    override val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var previousValidDomainSettings: Settings? = null
    private var previousDomainSettings: Settings? = null

    private val _pendingSave = MutableStateFlow<Settings?>(null)

    // ---------------------------------------------------------------------------------------------

    override suspend fun loadSettings() {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        try {
            val result = repository.getSettings()
            result.fold(
                onSuccess = { dto ->
                    dto.toDomainObject().onSuccess { domain ->
                        _state.value = domain.toState()
                        previousDomainSettings = domain
                        _uiState.value = UIState.Success(Unit)
                    }.onFailure { ex ->
                        _uiState.value = mapExceptionToUIState(ex)
                    }
                },
                onFailure = { throwable ->
                    _uiState.value = mapExceptionToUIState(throwable)
                }
            )
        } catch (ex: Exception) {
            _uiState.value = mapExceptionToUIState(ex)
        } finally {
            updateVolume()
            _isLoading.value = false
        }
    }

    override suspend fun updateSettings(state: SettingsState) {
        _state.value = state
        updateVolume()
        state.toDomainObject().onSuccess { domain ->
            if (previousValidDomainSettings == domain) {
                return
            }
            previousValidDomainSettings = domain
            _pendingSave.value = domain
        }
    }

    // ---------------------------------------------------------------------------------------------

    init {
        viewModelScope.launch {
            _pendingSave.debounce(1000).collectLatest { domain ->
                domain?.let {
                    repository.updateSettings(domain.toDTO())
                }
            }
        }
    }

    private fun updateVolume() {
        _state.value?.let {
            AudioManager.updateGlobalVolumeSFX(it.soundVolume)
            AudioManager.updateGlobalVolumeMusic(it.musicVolume)
        }
    }

}
