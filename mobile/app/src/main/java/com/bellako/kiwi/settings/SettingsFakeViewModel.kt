package com.bellako.kiwi.settings

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException

class SettingsFakeViewModel(
    private var backingState: SettingsState
) : ViewModel(), ISettingsViewModel {

    private val _state = MutableStateFlow<SettingsState?>(backingState)
    override val state: StateFlow<SettingsState?> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    private var currentDomainSettings: Settings? = backingState.toDomainObject().getOrNull()

    var simulateLoadError: Boolean = false
    var simulateUpdateError: Boolean = false
    var simulatedException: Exception = Exception("Something went wrong")


    override fun reset() {}

    override fun loadSettings() {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        if (simulateLoadError) {
            _uiState.value = mapExceptionToUIState(simulatedException)
        } else {
            _uiState.value = UIState.Success(Unit)
        }

        _isLoading.value = false
    }

    override fun updateSettings(state: SettingsState) {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        if (simulateUpdateError) {
            _isLoading.value = false
            _uiState.value = mapExceptionToUIState(simulatedException)
            return
        }

        val result = state.toDomainObject()

        result.onFailure {
            _uiState.value = mapExceptionToUIState(simulatedException)
        }.onSuccess { domain ->
            if (currentDomainSettings != domain) {
                currentDomainSettings = domain
                _state.value = domain.toState()
                _uiState.value = UIState.Success(Unit)
            }
        }

        _isLoading.value = false
    }

    private fun mapExceptionToUIState(e: Throwable): UIState<Unit> {
        return when (e) {
            is HttpException -> {
                if (e.code() >= 500) UIState.GeneralError
                else UIState.Error("Server error: ${e.message()}")
            }
            is IOException -> UIState.GeneralError
            else -> UIState.GeneralError
        }
    }
}