package com.bellako.kiwi.users

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.network.JwtAuthInterceptor
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository,
    private val jwtAuthInterceptor: JwtAuthInterceptor
) : ViewModel(), IUsersViewModel {
    private val _state = MutableStateFlow<UsersState?>(null)
    override val state: StateFlow<UsersState?> = _state.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override fun signup(state: UsersState) : Result<Unit> {
        return state.toDomainObject().fold(
            onSuccess = { user ->
                _isLoading.value = true
                try {
                    _isLoading.value = false
                    repository.signup(user.toDTO())
                } catch (e: Exception) {
                    _isLoading.value = false
                    Result.failure(e)
                }
            },
            onFailure = {
                Result.failure(Exception("Invalid email or password format"))
            }
        )
    }

    override fun login(state: UsersState): Result<Unit> {
        return state.toDomainObject().fold(
            onSuccess = { user ->
                _isLoading.value = true
                val apiResult: Result<String> = repository.login(user.toDTO())
                _isLoading.value = false

                return if (apiResult.isSuccess) {
                    jwtAuthInterceptor.setJwtToken(apiResult.getOrDefault(""))
                    Result.success(Unit)
                } else {
                    Result.failure(apiResult.exceptionOrNull() ?: Exception("Incorrect email or password"))
                }
            },
            onFailure = {
                Result.failure(Exception("Invalid email or password format"))
            }
        )
    }

    override fun onEmailChanged(email: String) {
        _state.value = _state.value?.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value?.copy(password = password)
    }
}