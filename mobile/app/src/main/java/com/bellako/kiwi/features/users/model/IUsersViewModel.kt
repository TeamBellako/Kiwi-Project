package com.bellako.kiwi.features.users.model

import android.content.Context
import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.users.data.UsersState
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface IUsersViewModel : IBaseViewModel<UsersState> {
    val initialAuthCheckPerformed: StateFlow<Boolean>

    val manualAuthOverlayActive: StateFlow<Boolean>

    fun markInitialAuthCheckPerformed()

    fun setManualAuthOverlayActive(active: Boolean)

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
