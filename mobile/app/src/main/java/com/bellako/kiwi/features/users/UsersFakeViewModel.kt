package com.bellako.kiwi.features.users

import com.bellako.kiwi.features.common.BaseFakeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsersFakeViewModel(
    initialState: UsersState,
) : BaseFakeViewModel(), IUsersViewModel {

    private val _state = MutableStateFlow<UsersState?>(initialState)
    override val state: StateFlow<UsersState?> = _state.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    override suspend fun signup(state: UsersState): Result<Unit> {
        setLoading(true)

        return if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }
    }

    override suspend fun login(state: UsersState): Result<Unit> {
        setLoading(true)

        return if (fakeError) {
            handleError(fakeException)
            Result.failure(fakeException)
        } else {
            handleSuccess()
            Result.success(Unit)
        }
    }

    override fun logout() {}

    override fun onEmailChanged(email: String) {
        _state.value = _state.value?.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value?.copy(password = password)
    }
}
