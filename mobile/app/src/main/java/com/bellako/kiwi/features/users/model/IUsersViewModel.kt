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

    /**
     * Tracks whether the LogInScreen has already run its initial credential
     * check this app session. Lets the screen distinguish a cold start (where
     * we want to redirect to sign-up if no credentials are stored) from
     * later visits (e.g. after a logout, or the user navigating back here
     * from the sign-up screen), where the user should stay on the login form.
     */
    fun hasAttemptedAutoLogin(): Boolean

    fun markAutoLoginAttempted()

    fun onEmailChanged(email: String)

    fun onPasswordChanged(password: String)

    fun checkEmailValid(): Boolean

    fun checkPasswordValid(): Boolean

    fun getRegisterDate(): LocalDate

    // ---------------------------------------------------------------------------------------------

    suspend fun signup(context: Context): Result<Unit>

    suspend fun login(context: Context): Result<Unit>

    suspend fun logout(context: Context)

    suspend fun getMyUserPoints()

    // ---------------------------------------------------------------------------------------------

    suspend fun saveLocalCredentials(context: Context)

    suspend fun getLocalCredentials(context: Context): Pair<String?, String?>

    suspend fun clearLocalCredentials(context: Context)
}
