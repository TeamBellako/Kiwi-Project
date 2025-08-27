package com.bellako.kiwi.features.settings.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.features.settings.data.SettingsDTO
import com.bellako.kiwi.features.settings.data.SettingsDataMapper
import com.bellako.kiwi.features.settings.data.SettingsDomain
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

        private var previousValidSettingsDomain: SettingsDomain? = null
        private var previousSettingsDomain: SettingsDomain? = null

        private val pendingSave = MutableStateFlow<SettingsDomain?>(null)

        // ---------------------------------------------------------------------------------------------

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun loadSettings() {
            setIsLoading(true)
            setUiState(UIState.Loading)

            repository.getSettings().fold(
                onSuccess = { dto ->
                    _state.value = SettingsDataMapper.toState(dto)
                    previousSettingsDomain = SettingsDataMapper.toDomain(dto)
                    setUiState(UIState.Success(Unit))
                },
                onFailure = { throwable ->
                    val dto = SettingsDTO(1f, 1f)
                    repository.updateSettings(dto).fold(
                        onSuccess = { dto ->
                            _state.value = SettingsDataMapper.toState(dto)
                            previousSettingsDomain = SettingsDataMapper.toDomain(dto)
                            setUiState(UIState.Success(Unit))
                        },
                        onFailure = { throwable ->
                            setUiState(mapExceptionToUIState(throwable))
                        },
                    )
                },
            )

            updateVolume()
            setIsLoading(false)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun updateSettings(state: SettingsState) {
            _state.value = state
            updateVolume()
            val domain = SettingsDataMapper.toDomain(state)
            if (previousValidSettingsDomain == domain) {
                return
            }
            previousValidSettingsDomain = domain
            pendingSave.value = domain
        }

        // ---------------------------------------------------------------------------------------------

        init {
            viewModelScope.launch {
                pendingSave.debounce(AUTO_SAVE_MILLIS).collectLatest { domain ->
                    domain?.let {
                        repository.updateSettings(SettingsDataMapper.toDTO(domain))
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
