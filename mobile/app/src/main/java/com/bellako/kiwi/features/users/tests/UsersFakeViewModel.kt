package com.bellako.kiwi.features.users.tests

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.users.data.Email
import com.bellako.kiwi.features.users.data.Password
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

@Suppress("EmptyFunctionBlock")
class UsersFakeViewModel(
    initialState: UsersState,
) : BaseFakeViewModel(),
    IUsersViewModel {
    private val _state = MutableStateFlow<UsersState?>(initialState)
    override val state: StateFlow<UsersState?> = _state.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    // ---------------------------------------------------------------------------------------------

    override fun onEmailChanged(email: String) {
        _state.value = _state.value?.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value?.copy(password = password)
    }

    override fun checkEmailValid(): Boolean =
        Email.of(_state.value!!.email).fold(
            onSuccess = { _ -> true },
            onFailure = { err ->
                setUiState(UIState.Error(err.message.orEmpty()))
                false
            },
        )

    override fun checkPasswordValid(): Boolean =
        Password.of(_state.value!!.password).fold(
            onSuccess = { _ -> true },
            onFailure = { err ->
                setUiState(UIState.Error(err.message.orEmpty()))
                false
            },
        )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getRegisterDate(): LocalDate = LocalDate.parse(_state.value?.registerDate)

    // ---------------------------------------------------------------------------------------------

    override suspend fun signup(context: Context): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            login(context)
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun login(context: Context): Result<Unit> =
        if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            saveLocalCredentials(context)
            handleSuccess()
            Result.success(Unit)
        }

    override suspend fun logout(context: Context) {}

    // ---------------------------------------------------------------------------------------------

    override suspend fun saveLocalCredentials(context: Context) {}

    override suspend fun getLocalCredentials(context: Context): Pair<String?, String?> = Pair("", "")

    override suspend fun clearLocalCredentials(context: Context) {}
}
