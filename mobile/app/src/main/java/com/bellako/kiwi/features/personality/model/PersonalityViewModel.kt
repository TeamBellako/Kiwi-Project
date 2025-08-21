package com.bellako.kiwi.features.personality.model

import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.features.personality.data.*
import com.bellako.kiwi.features.personality.data.Personality
import com.bellako.kiwi.features.personality.data.PersonalityBuildDTO
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.data.PersonalityUserNameDTO
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

        override val _isLoading = MutableStateFlow(false)
        override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private fun setIsLoading(isLoading: Boolean) {
            _isLoading.value = isLoading
            _uiState.value = if (isLoading) UIState.Loading else UIState.Idle
        }

        // ---------------------------------------------------------------------------------------------

        override suspend fun loadPersonality(): Result<Unit> {
            setIsLoading(true)
            return repository
                .getPersonality()
                .map { dto ->
                    val state = dto.toState()
                    _state.value =
                        _state.value.copy(
                            realName = state.realName,
                            knightName = state.knightName,
                            build = state.build,
                            goodApps = state.goodApps,
                            badApps = state.badApps,
                        )
                    setIsLoading(false)
                }.onFailure { throwable ->
                    setIsLoading(false)
                    _uiState.value = mapExceptionToUIState(throwable)
                }
        }

        override fun checkValid(): Result<Personality> =
            _state.value.toDomainObject().fold(
                onSuccess = { validState ->
                    Result.success(
                        Personality(validState.realName, validState.knightName, validState.build, validState.goodApps, validState.badApps),
                    )
                },
                onFailure = { err ->
                    _uiState.value = UIState.Error(err.message.orEmpty())
                    Result.failure(err)
                },
            )

        override suspend fun updateRealName(): Result<Unit> =
            handleResultSuspend(repository.updateRealName(PersonalityUserNameDTO(_state.value.realName))) {
            }

        override suspend fun updateKnightName(): Result<Unit> =
            handleResultSuspend(repository.updateKnightName(PersonalityUserNameDTO(_state.value.knightName))) {
            }

        override fun onRealNameChanged(name: String) {
            _state.value = _state.value.copy(realName = name)
        }

        override fun onKnightNameChanged(name: String) {
            _state.value = _state.value.copy(knightName = name)
        }

        override fun onAppsChanged(
            goodApps: List<String>,
            badApps: List<String>,
        ) {
            _state.value = _state.value.copy(goodApps = goodApps)
            _state.value = _state.value.copy(badApps = badApps)
        }

        private fun deduceBuild(): String =
            when (_state.value.answers.last()) {
                0 -> BERSERKER
                1 -> SHAMAN
                else -> MONK
            }

        override suspend fun updateBuild(): Result<Unit> {
            setIsLoading(true)
            _state.value = _state.value.copy(build = deduceBuild())
            return handleResultSuspend(repository.updateBuild(PersonalityBuildDTO(_state.value.build))) {
                setIsLoading(false)
                _uiState.value = UIState.Success(Unit)
            }
        }

        override suspend fun updateApps(): Result<Unit> {
            setIsLoading(true)
            return handleResultSuspend(repository.updateApps(PersonalityAppsDTO(_state.value.goodApps, _state.value.badApps))) {
                setIsLoading(false)
                _uiState.value = UIState.Success(Unit)
            }
        }
    }
