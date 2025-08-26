package com.bellako.kiwi.features.settings.tests

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.settings.data.SettingsDataMapper
import com.bellako.kiwi.features.settings.data.SettingsDomain
import com.bellako.kiwi.features.settings.data.SettingsState
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsFakeViewModel(
    backingState: SettingsState,
) : BaseFakeViewModel(),
    ISettingsViewModel {
    private val _state = MutableStateFlow<SettingsState?>(backingState)
    override val state: StateFlow<SettingsState?> = _state.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentSettingsDomain: SettingsDomain? = SettingsDataMapper.toDomain(backingState)

    var simulateLoadError: Boolean = false
    var simulateUpdateError: Boolean = false
    var simulatedException: Exception = Exception("Something went wrong")

    override suspend fun loadSettings() {
        setLoading(true)

        // Simulate an error or successful loading asynchronously
        if (simulateLoadError) {
            handleError(simulatedException)
        } else {
            handleSuccess()
        }

        setLoading(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateSettings(state: SettingsState) {
        setLoading(true)

        // Simulate an error or successful update asynchronously
        if (simulateUpdateError) {
            handleError(simulatedException)
            setLoading(false)
            return
        }

        val domain = SettingsDataMapper.toDomain(state)
        if (currentSettingsDomain != domain) {
            currentSettingsDomain = domain
            _state.value = state
            handleSuccess()
        }

        setLoading(false)
    }
}
