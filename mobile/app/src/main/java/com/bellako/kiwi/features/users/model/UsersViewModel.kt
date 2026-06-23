package com.bellako.kiwi.features.users.model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.analytics.firebaseSetUserId
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.users.data.Email
import com.bellako.kiwi.features.users.data.LoginDTO
import com.bellako.kiwi.features.users.data.Password
import com.bellako.kiwi.features.users.data.UsersState
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.GeneralSecurityException
import java.time.LocalDate

@HiltViewModel
class UsersViewModel
    @Inject
    constructor(
        private val repository: UsersRepository,
        private val authRepository: AuthRepository,
    ) : BaseViewModel(),
        IUsersViewModel {
        private val _state = MutableStateFlow(UsersState("", "", ""))
        override val state: StateFlow<UsersState> = _state.asStateFlow()

        private val _isLoginCompleted = MutableStateFlow(false)
        val isLoginCompleted: StateFlow<Boolean> = _isLoginCompleted.asStateFlow()

        private val _showAppLoading = MutableStateFlow(false)
        override val showAppLoading: StateFlow<Boolean> = _showAppLoading.asStateFlow()

        override fun setShowAppLoading(active: Boolean) {
            _showAppLoading.value = active
        }

        // Latches once on the first LogInScreen mount in this app session;
        // never reset (logout / manual nav back to LOGIN should NOT re-arm
        // the auto-redirect to sign-up).
        private var autoLoginAttempted: Boolean = false

        override fun hasAttemptedAutoLogin(): Boolean = autoLoginAttempted

        override fun markAutoLoginAttempted() {
            autoLoginAttempted = true
        }

        init {
            repository.currentPoints
                .onEach { points ->
                    _state.value = _state.value.copy(currentPoints = points)
                }.launchIn(viewModelScope)
        }

        // -----------------------------------------------------------------------------------------

        override fun onEmailChanged(email: String) {
            _state.value = _state.value.copy(email = email)
        }

        override fun onPasswordChanged(password: String) {
            _state.value = _state.value.copy(password = password)
        }

        override fun checkEmailValid(): Boolean =
            Email.of(_state.value.email).fold(
                onSuccess = { _ -> true },
                onFailure = { err ->
                    setUiState(UIState.Error(err.message.orEmpty()))
                    false
                },
            )

        // The "why" is surfaced inline next to the password field (see the
        // sign-up form), so this only needs to gate submission — no error is
        // pushed to the shared UI state here to avoid a duplicate message.
        override fun checkPasswordValid(): Boolean = Password.isValid(_state.value.password)

        @RequiresApi(Build.VERSION_CODES.O)
        override fun getRegisterDate(): LocalDate = stringToDate(_state.value.registerDate)

        // -----------------------------------------------------------------------------------------

        override suspend fun signup(context: Context): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            val result = repository.signup(LoginDTO(_state.value.email, _state.value.password))
            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                login(context)
            }
        }

        override suspend fun login(context: Context): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            val result = repository.login(LoginDTO(_state.value.email, _state.value.password))
            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                authRepository.setJwtToken(result.getOrThrow().jwt)
                _state.value = _state.value.copy(registerDate = result.getOrThrow().registerDate)
                saveLocalCredentials(context)
                firebaseSetUserId(_state.value.email)
                repository.getMyUserPoints()
                _isLoginCompleted.value = true
            }
        }

        override suspend fun logout(context: Context) {
            clearLocalCredentials(context)
            authRepository.setJwtToken("")
            _isLoginCompleted.value = false
        }

        // -----------------------------------------------------------------------------------------

        private val Context.dataStore by preferencesDataStore("secure_prefs")
        private val prefFileName = "prefs"
        private val keysetName = "usersKeyset"
        private val usernameDataKey = stringPreferencesKey("username_key")
        private val passwordDataKey = stringPreferencesKey("password_key")

        private lateinit var aEAD: Aead

        private fun initAEAD(context: Context) {
            AeadConfig.register()

            val manager =
                AndroidKeysetManager
                    .Builder()
                    .withSharedPref(context, keysetName, prefFileName)
                    .withKeyTemplate(AeadKeyTemplates.AES128_GCM)
                    .build()

            val keysetHandle: KeysetHandle = manager.keysetHandle
            aEAD = keysetHandle.getPrimitive(ConfigurationV0.get(), Aead::class.java)
        }

        override suspend fun saveLocalCredentials(context: Context) {
            setIsLoading(true)
            setUiState(UIState.Loading)
            try {
                initAEAD(context)
                context.dataStore.edit { prefs ->
                    val emailEncrypted = aEAD.encrypt(state.value.email.toByteArray(), null)
                    prefs[usernameDataKey] =
                        android.util.Base64.encodeToString(emailEncrypted, android.util.Base64.DEFAULT)
                    val passwordEncrypted = aEAD.encrypt(state.value.password.toByteArray(), null)
                    prefs[passwordDataKey] =
                        android.util.Base64.encodeToString(
                            passwordEncrypted,
                            android.util.Base64.DEFAULT,
                        )
                }
            } catch (e: GeneralSecurityException) {
                warn("Encryption error: ${e.message}")
            } catch (e: IOException) {
                warn("DataStore error: ${e.message}")
            } finally {
                setIsLoading(false)
                setUiState(UIState.Idle)
            }
        }

        override suspend fun getLocalCredentials(context: Context): Pair<String?, String?> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            return try {
                initAEAD(context)
                val prefs = context.dataStore.data.first()
                val emailEncrypted = prefs[usernameDataKey]
                val passwordEncrypted = prefs[passwordDataKey]

                if (emailEncrypted == null || passwordEncrypted == null) {
                    "" to ""
                } else {
                    val emailDecrypted =
                        aEAD.decrypt(
                            android.util.Base64.decode(emailEncrypted, android.util.Base64.DEFAULT),
                            null,
                        )
                    val passwordDecrypted =
                        aEAD.decrypt(
                            android.util.Base64.decode(
                                passwordEncrypted,
                                android.util.Base64.DEFAULT,
                            ),
                            null,
                        )
                    String(emailDecrypted) to String(passwordDecrypted)
                }
            } catch (e: GeneralSecurityException) {
                warn("Decryption error: ${e.message}")
                "" to ""
            } catch (e: IOException) {
                warn("DataStore error: ${e.message}")
                "" to ""
            } catch (e: IllegalArgumentException) {
                warn("Base64 decoding error: ${e.message}")
                "" to ""
            } finally {
                setIsLoading(false)
                setUiState(UIState.Idle)
            }
        }

        @Suppress("TooGenericExceptionCaught")
        override suspend fun clearLocalCredentials(context: Context) {
            setIsLoading(true)
            setUiState(UIState.Loading)
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
                setUiState(UIState.Idle)
            }
        }

        override suspend fun getMyUserPoints() {
            setIsLoading(true)
            val result = repository.getMyUserPoints()
            setIsLoading(false)
            result
                .onSuccess { points ->
                    _state.value =
                        _state.value.copy(
                            currentPoints = points.currentPoints,
                            totalPoints = points.totalPoints,
                        )
                }.onFailure { err ->
                    setUiState(UIState.Error(err.message.orEmpty()))
                }
        }
    }
