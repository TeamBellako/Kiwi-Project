package com.bellako.kiwi.users

import kotlinx.coroutines.flow.StateFlow

interface IUsersViewModel {
    val state: StateFlow<UsersState?>
    val isLoading: StateFlow<Boolean>

    suspend fun signup(state: UsersState): Result<Unit>
    suspend fun login(state: UsersState): Result<Unit>
}