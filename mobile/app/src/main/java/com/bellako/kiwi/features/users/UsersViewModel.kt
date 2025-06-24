package com.bellako.kiwi.features.users


import com.bellako.kiwi.services.common.BaseViewModel
import com.bellako.kiwi.services.network.AuthRepository
import com.bellako.kiwi.services.common.UIState
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

    override fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    override fun logout() {
        authRepository.setJwtToken("")
    }

    override suspend fun signup(state: UsersState): Result<Unit> {
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

        return handleResultSuspend(result) {
            login(state)
        }
    }

    override suspend fun login(state: UsersState): Result<Unit> {
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

        return handleResultSuspend(result) {
            authRepository.setJwtToken(result.getOrThrow())
        }
    }

    private fun getInvalidDataMessage(): String = """
        Invalid email or password. Password must:
        - Be at least 8 characters long
        - Include both uppercase and lowercase letters
        - Contain at least one number
        - Contain at least one special character
    """.trimIndent()
}
