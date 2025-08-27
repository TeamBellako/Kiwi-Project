package com.bellako.kiwi.features.personality.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.features.personality.data.BERSERKER
import com.bellako.kiwi.features.personality.data.MONK
import com.bellako.kiwi.features.personality.data.PersonalityAppsDTO
import com.bellako.kiwi.features.personality.data.PersonalityBuildDTO
import com.bellako.kiwi.features.personality.data.PersonalityDataMapper
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.data.PersonalityUserNameDTO
import com.bellako.kiwi.features.personality.data.SHAMAN
import com.bellako.kiwi.features.personality.data.UserName
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class PersonalityViewModel
    @Inject
    constructor(
        private val repository: PersonalityRepository,
    ) : BaseViewModel(),
        IPersonalityViewModel {
        private val _state = MutableStateFlow(PersonalityState("", "", "", listOf(), listOf()))
        override val state: StateFlow<PersonalityState?> = _state.asStateFlow()

        // -----------------------------------------------------------------------------------------

        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun loadPersonality(): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)

            return repository
                .getPersonality()
                .map { dto ->
                    val state = PersonalityDataMapper.toState(dto)
                    _state.value =
                        _state.value.copy(
                            realName = state.realName,
                            knightName = state.knightName,
                            build = state.build,
                            goodApps = state.goodApps,
                            badApps = state.badApps,
                        )
                    setIsLoading(false)
                    setUiState(UIState.Idle)
                }.onFailure { throwable ->
                    setIsLoading(false)
                    setUiState(mapExceptionToUIState(throwable))
                }
        }

        // -----------------------------------------------------------------------------------------

        override fun onRealNameChanged(name: String) {
            _state.value = _state.value.copy(realName = name)
        }

        override fun onKnightNameChanged(name: String) {
            _state.value = _state.value.copy(knightName = name)
        }

        override fun checkRealNameValid(): Boolean = checkNameValid(_state.value.realName)

        override fun checkKnightNameValid(): Boolean = checkNameValid(_state.value.knightName)

        private fun checkNameValid(name: String): Boolean =
            UserName.of(name).fold(
                onSuccess = { _ -> true },
                onFailure = { err ->
                    setUiState(UIState.Error(err.message.orEmpty()))
                    false
                },
            )

        override suspend fun updateRealName(): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            val result = repository.updateRealName(PersonalityUserNameDTO(_state.value.realName))
            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                setUiState(UIState.Success(Unit))
            }
        }

        override suspend fun updateKnightName(): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            val result = repository.updateKnightName(PersonalityUserNameDTO(_state.value.knightName))
            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                setUiState(UIState.Success(Unit))
            }
        }

        // -----------------------------------------------------------------------------------------

        private fun deduceBuild(): String =
            when (_state.value.answers.last()) {
                0 -> BERSERKER
                1 -> SHAMAN
                else -> MONK
            }

        override suspend fun updateBuild(): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            _state.value = _state.value.copy(build = deduceBuild())
            val result = repository.updateBuild(PersonalityBuildDTO(_state.value.build))
            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                setUiState(UIState.Success(Unit))
            }
        }

        // -----------------------------------------------------------------------------------------

        override fun onAppsChanged(
            goodApps: List<String>,
            badApps: List<String>,
        ) {
            _state.value = _state.value.copy(goodApps = goodApps)
            _state.value = _state.value.copy(badApps = badApps)
        }

        override suspend fun updateApps(): Result<Unit> {
            setIsLoading(true)
            setUiState(UIState.Loading)
            val result =
                repository.updateApps(
                    PersonalityAppsDTO(_state.value.goodApps, _state.value.badApps),
                )
            setIsLoading(false)
            setUiState(UIState.Idle)

            return handleResultSuspend(result) {
                setUiState(UIState.Success(Unit))
            }
        }
    }
