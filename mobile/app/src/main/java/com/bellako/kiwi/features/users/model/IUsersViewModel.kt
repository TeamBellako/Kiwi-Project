package com.bellako.kiwi.features.users.model

import android.content.Context
import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.users.data.UsersState
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface IUsersViewModel : IBaseViewModel<UsersState> {
    /**
     * Drives the full-screen map-entry loading curtain. Raised the moment a
     * map-bound action begins (manual log in, auto log in once stored
     * credentials are found, the app-selection Confirm) and lowered by
     * [com.bellako.kiwi.common.screens.MainScreen] once the map's data is ready.
     */
    val showAppLoading: StateFlow<Boolean>

    fun setShowAppLoading(active: Boolean)

    fun onEmailChanged(email: String)

    fun onPasswordChanged(password: String)

    fun checkEmailValid(): Boolean

    fun checkPasswordValid(): Boolean

    fun getRegisterDate(): LocalDate

    // ---------------------------------------------------------------------------------------------

    suspend fun signup(context: Context): Result<Unit>

    suspend fun login(context: Context): Result<Unit>

    suspend fun logout(context: Context)

    // ---------------------------------------------------------------------------------------------

    suspend fun saveLocalCredentials(context: Context)

    suspend fun getLocalCredentials(context: Context): Pair<String?, String?>

    suspend fun clearLocalCredentials(context: Context)
}
