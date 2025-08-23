package com.bellako.kiwi.features.users.tests

import android.content.Context
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    override suspend fun saveLocalCredentials(context: Context) {}

    override suspend fun getLocalCredentials(context: Context): Pair<String?, String?> = Pair("", "")

    override suspend fun clearLocalCredentials(context: Context) {}
}
