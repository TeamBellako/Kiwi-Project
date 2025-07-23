package com.bellako.kiwi.features.users

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bellako.kiwi.services.common.BaseViewModel
import com.bellako.kiwi.services.common.Logger.warn
import com.bellako.kiwi.services.network.AuthRepository
import com.bellako.kiwi.services.common.UIState
import com.bellako.kiwi.types.Email
import com.bellako.kiwi.types.Password
import com.google.crypto.tink.Aead
import com.google.crypto.tink.ConfigurationV0
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository,
    private val authRepository: AuthRepository
) : BaseViewModel(), IUsersViewModel {

    private val _state = MutableStateFlow(UsersState("", ""))
    override val state: StateFlow<UsersState> = _state.asStateFlow()

    private val _isLoginCompleted = MutableStateFlow(false);
    val isLoginCompleted : StateFlow<Boolean> = _isLoginCompleted.asStateFlow();

    private fun setIsLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
        _uiState.value = if (isLoading) UIState.Loading else UIState.Idle
    }

    // ---------------------------------------------------------------------------------------------

    override fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    // ---------------------------------------------------------------------------------------------

    override suspend fun signup(context: Context): Result<Unit> {
        setIsLoading(true)
        val result = repository.signup(UsersDTO(_state.value.email, _state.value.password))
        setIsLoading(false)

        return handleResultSuspend(result) {
            login(context)
        }
    }

    override suspend fun login(context: Context): Result<Unit> {
        setIsLoading(true)
        val result = repository.login(UsersDTO(_state.value.email, _state.value.password))
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

    // ---------------------------------------------------------------------------------------------

    private val Context.dataStore by preferencesDataStore("secure_prefs")
    private val prefFileName = "prefs"
    private val keysetName = "usersKeyset"
    private val usernameDataKey = stringPreferencesKey("username_key")
    private val passwordDataKey = stringPreferencesKey("password_key")

    private lateinit var aEAD: Aead

    private fun initAEAD(context: Context) {
        AeadConfig.register()

        val manager = AndroidKeysetManager.Builder()
            .withSharedPref(context, keysetName, prefFileName)
            .withKeyTemplate(AeadKeyTemplates.AES128_GCM)
            .build()

        val keysetHandle: KeysetHandle = manager.keysetHandle
        aEAD = keysetHandle.getPrimitive(ConfigurationV0.get(), Aead::class.java)
    }

    override suspend fun saveLocalCredentials(context: Context) {
        setIsLoading(true)
        try {
            initAEAD(context)
            context.dataStore.edit { prefs ->
                val emailEncrypted = aEAD.encrypt(state.value.email.toByteArray(), null)
                prefs[usernameDataKey] =
                    android.util.Base64.encodeToString(emailEncrypted, android.util.Base64.DEFAULT)
                val passwordEncrypted = aEAD.encrypt(state.value.password.toByteArray(), null)
                prefs[passwordDataKey] = android.util.Base64.encodeToString(
                    passwordEncrypted,
                    android.util.Base64.DEFAULT
                )
            }
        } catch (e: Exception) {
            warn(e.message.orEmpty())
        } finally {
            setIsLoading(false)
        }
    }

    override suspend fun getLocalCredentials(context: Context): Pair<String?, String?> {
        setIsLoading(true)
        try {
            initAEAD(context)
            val prefs = context.dataStore.data.first()
            val emailEncrypted = prefs[usernameDataKey] ?: return Pair("", "")
            val passwordEncrypted = prefs[passwordDataKey] ?: return Pair("", "")
            val emailDecrypted = aEAD.decrypt(android.util.Base64.decode(emailEncrypted, android.util.Base64.DEFAULT), null)
            val passwordDecrypted = aEAD.decrypt(android.util.Base64.decode(passwordEncrypted, android.util.Base64.DEFAULT), null)
            return Pair(String(emailDecrypted), String(passwordDecrypted))
        } catch (e: Exception) {
            warn(e.message.orEmpty())
            return Pair("", "")
        } finally {
            setIsLoading(false)
        }
    }

    override suspend fun clearLocalCredentials(context: Context) {
        setIsLoading(true)
        try {
            initAEAD(context)
            context.dataStore.edit { prefs ->
                prefs.remove(usernameDataKey)
                prefs.remove(passwordDataKey)
            }
        } catch (e: Exception) {
            warn(e.message.orEmpty())
        } finally {
            setIsLoading(false)
        }
    }

    // ---------------------------------------------------------------------------------------------

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
