package com.bellako.kiwi.login

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.common.UIState
import com.bellako.kiwi.network.AuthRepository
import com.bellako.kiwi.utils.HTTPUtils.extractHttpExceptionMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val authRepository: AuthRepository,
) : ViewModel(), ILoginViewModel {

    private val _state = MutableStateFlow<LoginState?>(LoginState("", ""))
    override val state: StateFlow<LoginState?> = _state.asStateFlow()

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    override val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override fun onEmailChanged(email: String) {
        _state.value = _state.value?.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value?.copy(password = password)
    }

    override fun logout() {
        authRepository.setJwtToken("")
    }

    override suspend fun signup(state: LoginState): Result<Unit> {
        val domainResult = state.toDomainObject()
        if (domainResult.isFailure) {
            val message = getInvalidDataMessage()
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }

        val user = domainResult.getOrThrow()
        _isLoading.value = true
        _uiState.value = UIState.Loading

        val result = repository.signup(user.toDTO())
        _isLoading.value = false

        return result.fold(
            onSuccess = {
                _uiState.value = UIState.Success(Unit)
                login(state)
            },
            onFailure = { throwable ->
                _uiState.value = when (throwable) {
                    is HttpException -> {
                        if (throwable.code() >= 500) UIState.GeneralError
                        else UIState.Error(extractHttpExceptionMessage(throwable))
                    }
                    else -> UIState.GeneralError
                }
                Result.failure(throwable)
            }
        )
    }

    override suspend fun login(state: LoginState): Result<Unit> {
        val domainResult = state.toDomainObject()
        if (domainResult.isFailure) {
            val message = getInvalidDataMessage()
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }

        val user = domainResult.getOrThrow()
        _isLoading.value = true
        _uiState.value = UIState.Loading

        val result = repository.login(user.toDTO())
        _isLoading.value = false

        return result.fold(
            onSuccess = {
                authRepository.setJwtToken(it)
                _uiState.value = UIState.Success(Unit)
                Result.success(Unit)
            },
            onFailure = { throwable ->
                _uiState.value = when (throwable) {
                    is HttpException -> {
                        if (throwable.code() >= 500) UIState.GeneralError
                        else UIState.Error(extractHttpExceptionMessage(throwable))
                    }
                    else -> UIState.GeneralError
                }
                Result.failure(throwable)
            }
        )
    }

    private fun getInvalidDataMessage(): String {
        return """
            Invalid email or password. Password must:
            - Be at least 8 characters long
            - Include both uppercase and lowercase letters
            - Contain at least one number
            - Contain at least one special character
        """.trimIndent()
    }
}
