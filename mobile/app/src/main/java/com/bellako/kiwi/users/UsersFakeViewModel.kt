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

    var fakeError: Boolean = false

    override fun signup(state: UsersState): Result<Unit> {
        if (fakeError) {
            return Result.failure(Exception("Signup error"))
        }

        return Result.success(Unit)
    }

    override fun login(state: UsersState): Result<Unit> {
        if (fakeError) {
            return Result.failure(Exception("Login error"))
        }

        return Result.success(Unit)
    }


    override fun onEmailChanged(email: String) {
        _state.value = _state.value?.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value?.copy(password = password)
    }
}