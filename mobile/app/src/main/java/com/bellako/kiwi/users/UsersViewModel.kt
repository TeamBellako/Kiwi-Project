package com.bellako.kiwi.users

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.userSettings.network.UserSettingsRepository
import com.bellako.kiwi.userSettings.types.UserSettingsState
import com.bellako.kiwi.userSettings.types.UserSettingsValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel(), IUsersViewModel {
    private val _state = MutableStateFlow<UsersState?>(null)
    override val state: StateFlow<UsersState?> = _state.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override fun signup(state: UsersState) {
        TODO("Not yet implemented")
    }

    override fun login(state: UsersState) {
        TODO("Not yet implemented")
    }
}