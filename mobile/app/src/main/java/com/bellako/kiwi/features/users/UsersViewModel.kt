package com.bellako.kiwi.features.users

import com.bellako.kiwi.services.common.BaseViewModel
import com.bellako.kiwi.services.network.AuthRepository
import com.bellako.kiwi.services.common.UIState
import com.bellako.kiwi.types.Email
import com.bellako.kiwi.types.Password
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository,
    private val authRepository: AuthRepository
) : BaseViewModel(), IUsersViewModel {

    private val _state = MutableStateFlow(UsersState("", ""))
    override val state: StateFlow<UsersState> = _state.asStateFlow()

    private val _isLoginCompleted = MutableStateFlow(false);
    val isLoginCompleted : StateFlow<Boolean> = _isLoginCompleted.asStateFlow();

    override fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    override suspend fun signup(state: UsersState): Result<Unit> {
        val domainResult = state.toDomainObject()
        if (domainResult.isFailure) {
            val message = getInvalidSignUpMessage(state)
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }

        val user = domainResult.getOrThrow()
        _isLoading.value = true
        _uiState.value = UIState.Loading

        val result = repository.signup(user.toDTO())
        _isLoading.value = false

        return handleResultSuspend(result) {
            login(state)
        }
    }

    override suspend fun login(state: UsersState): Result<Unit> {
        val domainResult = state.toDomainObject()
        if (domainResult.isFailure) {
            val message = getInvalidLoginMessage()
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }

        val user = domainResult.getOrThrow()
        _isLoading.value = true
        _uiState.value = UIState.Loading

        val result = repository.login(user.toDTO())
        _isLoading.value = false

        return handleResultSuspend(result) {
            authRepository.setJwtToken(result.getOrThrow())
            _isLoginCompleted.value = true
        }
    }

    override fun logout() {
        authRepository.setJwtToken("")
    }

    private fun getInvalidSignUpMessage(state: UsersState): String {
        Email.of(state.email).onFailure { ex ->
           return ex.message.orEmpty()
        }
        Password.of(state.password).onFailure { ex ->
           return ex.message.orEmpty()
        }
        return "Invalid email or password".trimIndent()
    }

    private fun getInvalidLoginMessage(): String = "Invalid email or password".trimIndent()
}
