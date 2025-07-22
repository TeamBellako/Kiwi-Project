package com.bellako.kiwi.features.users

import android.content.Context
import com.bellako.kiwi.features.common.IBaseViewModel

interface IUsersViewModel : IBaseViewModel<UsersState> {

    fun onEmailChanged(email: String)
    fun onPasswordChanged(password: String)

    suspend fun signup(context: Context): Result<Unit>
    suspend fun login(context: Context): Result<Unit>
    suspend fun logout(context: Context)

    fun saveLocalCredentials(context: Context)
    fun getLocalCredentials(context: Context): Pair<String?, String?>
    fun clearLocalCredentials(context: Context)
}