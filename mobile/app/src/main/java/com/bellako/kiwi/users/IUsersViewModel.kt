package com.bellako.kiwi.users

import kotlinx.coroutines.flow.StateFlow

interface IUsersViewModel {
    val state: StateFlow<UsersState?>
    val isLoading: StateFlow<Boolean>

    fun signup(state: UsersState): Result<Unit>
    fun login(state: UsersState): Result<Unit>

    fun onEmailChanged(email: String)
    fun onPasswordChanged(password: String)
}