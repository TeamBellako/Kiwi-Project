package com.bellako.kiwi.users

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.network.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository,
    private val authRepository: AuthRepository,
) : ViewModel(), IUsersViewModel {
    private val _state = MutableStateFlow<UsersState?>(UsersState("", ""))
    override val state: StateFlow<UsersState?> = _state.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override suspend fun signup(state: UsersState): Result<Unit> {
        val domainResult = state.toDomainObject()
        if (domainResult.isFailure) {
            return Result.failure(Exception("Invalid email or password format"))
        }

        val user = domainResult.getOrThrow()
        _isLoading.value = true

        return try {
            val result = repository.signup(user.toDTO())
            _isLoading.value = false
            result
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }


    override suspend fun login(state: UsersState): Result<Unit> {
        val userResult = state.toDomainObject()
        if (userResult.isFailure) {
            return Result.failure(Exception(getInvalidDataMessage()))
        }

        _isLoading.value = true
        val user = userResult.getOrThrow()

        val apiResult = repository.login(user.toDTO())
        _isLoading.value = false

        return apiResult.fold(
            onSuccess = { jwt ->
                authRepository.setJwtToken(jwt)
                Result.success(Unit)
            },
            onFailure = { Result.failure(Exception(getInvalidDataMessage())) }
        )
    }

    override fun onEmailChanged(email: String) {
        _state.value = _state.value?.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value?.copy(password = password)
    }

    private fun getInvalidDataMessage() : String {
        return """
            Incorrect email or password. Password must:
            - Be at least 8 characters long
            - Include both uppercase and lowercase letters
            - Contain at least one number
            - Contain at least one special character
        """.trimIndent()
    }
}