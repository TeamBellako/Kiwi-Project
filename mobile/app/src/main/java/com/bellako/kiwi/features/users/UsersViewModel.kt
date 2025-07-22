package com.bellako.kiwi.features.users

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
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

    private fun setIsLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
        _uiState.value = if (isLoading) UIState.Loading else UIState.Idle
    }

    override suspend fun signup(context: Context): Result<Unit> {
        val domainResult = _state.value.toDomainObject()
        if (domainResult.isFailure) {
            val message = getInvalidSignUpMessage()
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }
        val user = domainResult.getOrThrow()

        setIsLoading(true)

        val result = repository.signup(user.toDTO())

        setIsLoading(false)

        return handleResultSuspend(result) {
            login(context)
        }
    }

    override suspend fun login(context: Context): Result<Unit> {
        val domainResult = _state.value.toDomainObject()
        if (domainResult.isFailure) {
            val message = getInvalidLoginMessage()
            _uiState.value = UIState.Error(message)
            return Result.failure(Exception(message))
        }
        val user = domainResult.getOrThrow()

        setIsLoading(true)

        val result = repository.login(user.toDTO())

        setIsLoading(false)

        return handleResultSuspend(result) {
            authRepository.setJwtToken(result.getOrThrow())
            saveLocalCredentials(context)
            _isLoginCompleted.value = true
        }
    }

    override suspend fun logout(context: Context) {
        clearLocalCredentials(context)
        authRepository.setJwtToken("")
    }

    private val encryptedPrefsFile = "encrypted_prefs"
    private val encryptedUsernameKey = "username_key"
    private val encryptedPasswordKey = "password_key"

    private fun getEncryptedSharedPreferences(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            encryptedPrefsFile,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    override fun saveLocalCredentials(context: Context) {
        val prefs = getEncryptedSharedPreferences(context)
        prefs.edit().apply {
            putString(encryptedUsernameKey, state.value.email)
            putString(encryptedPasswordKey, state.value.password)
            apply()
        }
    }

    override fun getLocalCredentials(context: Context): Pair<String?, String?> {
        val prefs = getEncryptedSharedPreferences(context)
        val username = prefs.getString(encryptedUsernameKey, null)
        val password = prefs.getString(encryptedPasswordKey, null)
        return Pair(username, password)
    }

    override fun clearLocalCredentials(context: Context) {
        val prefs = getEncryptedSharedPreferences(context)
        prefs.edit().apply {
            remove(encryptedUsernameKey)
            remove(encryptedPasswordKey)
            apply()
        }
    }

    private fun getInvalidSignUpMessage(): String {
        Email.of(_state.value.email).onFailure { ex ->
           return ex.message.orEmpty()
        }
        Password.of(_state.value.password).onFailure { ex ->
           return ex.message.orEmpty()
        }
        return "Invalid email or password".trimIndent()
    }

    private fun getInvalidLoginMessage(): String = "Invalid email or password".trimIndent()
}
