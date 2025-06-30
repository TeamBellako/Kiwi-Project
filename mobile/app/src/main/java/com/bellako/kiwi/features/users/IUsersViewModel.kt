package com.bellako.kiwi.features.users

import com.bellako.kiwi.features.common.IBaseViewModel

interface IUsersViewModel : IBaseViewModel<UsersState> {
    suspend fun signup(state: UsersState): Result<Unit>
    suspend fun login(state: UsersState): Result<Unit>
    fun logout()

    fun onEmailChanged(email: String)
    fun onPasswordChanged(password: String)
}