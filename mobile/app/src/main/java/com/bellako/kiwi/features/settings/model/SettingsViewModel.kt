package com.bellako.kiwi.features.settings.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.features.settings.data.Settings
import com.bellako.kiwi.features.settings.data.SettingsState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

private const val AUTO_SAVE_MILLIS: Long = 1000

@OptIn(FlowPreview::class)
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val repository: SettingsRepository,
    ) : BaseViewModel(),
        ISettingsViewModel {
        private val _state = MutableStateFlow<SettingsState?>(null)
        override val state: StateFlow<SettingsState?> = _state.asStateFlow()

        private var previousValidDomainSettings: Settings? = null
        private var previousDomainSettings: Settings? = null

        private val pendingSave = MutableStateFlow<Settings?>(null)

        // ---------------------------------------------------------------------------------------------

        override suspend fun loadSettings() {
            setIsLoading(true)
            setUiState(UIState.Loading)

            try {
                val result = repository.getSettings()
                result.fold(
                    onSuccess = { dto ->
                        dto
                            .toDomainObject()
                            .onSuccess { domain ->
                                _state.value = domain.toState()
                                previousDomainSettings = domain
                                setUiState(UIState.Success(Unit))
                            }.onFailure { ex ->
                                setUiState(mapExceptionToUIState(ex))
                            }
                    },
                    onFailure = { throwable ->
                        setUiState(mapExceptionToUIState(throwable))
                    },
                )
            } catch (ex: HttpException) {
                setUiState(mapExceptionToUIState(ex))
            } finally {
                updateVolume()
                setIsLoading(false)
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
                pendingSave.value = domain
            }
        }

        // ---------------------------------------------------------------------------------------------

        init {
            viewModelScope.launch {
                pendingSave.debounce(AUTO_SAVE_MILLIS).collectLatest { domain ->
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
