package com.bellako.kiwi.users

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsersFakeViewModel  (
    initialState: UsersState,
    isLoading: Boolean
) : ViewModel(), IUsersViewModel{
    private val _state = MutableStateFlow<UsersState?>(initialState)
    override val state: StateFlow<UsersState?> = _state.asStateFlow()
    private val _isLoading = MutableStateFlow(isLoading)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override fun signup(state: UsersState): Result<Unit> {
        println("signup called with state: $state")
        return Result.success(Unit)
    }

    override fun login(state: UsersState): Result<Unit> {
        println("login called with state: $state")
        return Result.success(Unit)
    }


    override fun onEmailChanged(email: String) {
        _state.value = _state.value?.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value?.copy(password = password)
    }
}