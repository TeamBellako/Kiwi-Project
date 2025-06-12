package com.bellako.kiwi.login

import com.bellako.kiwi.common.UIState
import kotlinx.coroutines.flow.StateFlow

interface ILoginViewModel {
    val state: StateFlow<LoginState?>
    val isLoading: StateFlow<Boolean>
    val uiState: StateFlow<UIState<Unit>>

    suspend fun signup(state: LoginState): Result<Unit>
    suspend fun login(state: LoginState): Result<Unit>

    fun logout()

    fun onEmailChanged(email: String)
    fun onPasswordChanged(password: String)
}