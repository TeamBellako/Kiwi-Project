package com.bellako.kiwi.users

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel(), IUsersViewModel {
    private val _state = MutableStateFlow<UsersState?>(null)
    override val state: StateFlow<UsersState?> = _state.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override suspend fun signup(state: UsersState) : Result<Unit> {
        return state.toDomainObject().fold(
            onSuccess = { user ->
                _isLoading.value = true
                try {
                    val apiResult : Result<Unit> = repository.signup(user.toDTO())
                    _isLoading.value = false
                    apiResult
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

    override suspend fun login(state: UsersState): Result<String> {
        return state.toDomainObject().fold(
            onSuccess = { user ->
                _isLoading.value = true
                try {
                    val apiResult : Result<String> = repository.login(user.toDTO())
                    _isLoading.value = false
                    apiResult
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

}