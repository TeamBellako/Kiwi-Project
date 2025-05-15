package com.bellako.kiwi.users

import com.bellako.kiwi.userSettings.types.UserSettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IUsersViewModel {
    val state: StateFlow<UsersState?>
    val isLoading: StateFlow<Boolean>

    fun login(state: UsersState)
    fun signup(state: UsersState)
}