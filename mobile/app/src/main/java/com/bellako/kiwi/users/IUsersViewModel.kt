package com.bellako.kiwi.users

import com.bellako.kiwi.common.UIState
import kotlinx.coroutines.flow.StateFlow

interface IUsersViewModel {
    val state: StateFlow<UsersState?>
    val isLoading: StateFlow<Boolean>
    val uiState: StateFlow<UIState<Unit>>

    suspend fun signup(state: UsersState): Result<Unit>
    suspend fun login(state: UsersState): Result<Unit>

    fun onEmailChanged(email: String)
    fun onPasswordChanged(password: String)
}