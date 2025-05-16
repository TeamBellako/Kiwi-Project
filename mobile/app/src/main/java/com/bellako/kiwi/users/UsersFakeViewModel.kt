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
        TODO("Not yet implemented")
    }

    override fun login(state: UsersState): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun onEmailChanged(email: String) {
        TODO("Not yet implemented")
    }

    override fun onPasswordChanged(password: String) {
        TODO("Not yet implemented")
    }

}