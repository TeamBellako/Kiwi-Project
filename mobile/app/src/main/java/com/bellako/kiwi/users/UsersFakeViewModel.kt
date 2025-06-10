package com.bellako.kiwi.users

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException

class UsersFakeViewModel(
    initialState: UsersState,
    isLoading: Boolean
) : ViewModel(), IUsersViewModel {

    private val _state = MutableStateFlow<UsersState?>(initialState)
    override val state: StateFlow<UsersState?> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(isLoading)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    override suspend fun signup(state: UsersState): Result<Unit> {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        return if (fakeError) {
            _isLoading.value = false
            _uiState.value = mapExceptionToUIState(fakeException)
            Result.failure(fakeException)
        } else {
            _isLoading.value = false
            _uiState.value = UIState.Success(Unit)
            Result.success(Unit)
        }
    }

    override suspend fun login(state: UsersState): Result<Unit> {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        return if (fakeError) {
            _isLoading.value = false
            _uiState.value = mapExceptionToUIState(fakeException)
            Result.failure(fakeException)
        } else {
            _isLoading.value = false
            _uiState.value = UIState.Success(Unit)
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

    private fun mapExceptionToUIState(e: Throwable): UIState<Unit> {
        return when (e) {
            is HttpException -> {
                if (e.code() >= 500) UIState.GeneralError
                else UIState.Error("Server error: ${e.message()}")
            }
            is IOException -> UIState.GeneralError
            else -> UIState.GeneralError
        }
    }
}
