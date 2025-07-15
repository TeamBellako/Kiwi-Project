package com.bellako.kiwi.features.personality

import com.bellako.kiwi.services.common.BaseViewModel
import com.bellako.kiwi.services.network.AuthRepository
import com.bellako.kiwi.services.common.UIState
import com.bellako.kiwi.types.BERSERKER
import com.bellako.kiwi.types.MONK
import com.bellako.kiwi.types.SHAMAN
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class PersonalityViewModel @Inject constructor(
    private val repository: PersonalityRepository,
    private val authRepository: AuthRepository,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel(), IPersonalityViewModel {

    private val _state = MutableStateFlow(PersonalityState("", "", ""))
    override val state: StateFlow<PersonalityState?> = _state.asStateFlow()

    override val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var previousValidDomain: Personality? = null
    private var previousDomain: Personality? = null


    override fun reset() {
        previousValidDomain = null
    }

    override suspend fun loadPersonality(): Result<Unit> {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        return repository.getPersonality().map { dto ->
            val state = dto.toState()
            _state.value = _state.value.copy(realName = state.realName, knightName = state.knightName, build = state.build)
            _isLoading.value = false
        } .onFailure { throwable ->
            _isLoading.value = false
            _uiState.value = mapExceptionToUIState(throwable)
        }
    }

    override fun checkValid(): Result<Personality> {
        return _state.value.toDomainObject().fold(
            onSuccess = { validState ->
                Result.success(Personality(validState.realName, validState.knightName, validState.build))
            },
            onFailure = { err ->
                _uiState.value = UIState.Error(err.message.orEmpty())
                Result.failure(err)
            }
        )
    }

    override suspend fun updateRealName(): Result<Unit> {
        return handleResultSuspend(repository.updateRealName(PersonalityUserNameDTO(_state.value.realName))) {}
    }

    override suspend fun updateKnightName(): Result<Unit> {
        return handleResultSuspend(repository.updateKnightName(PersonalityUserNameDTO(_state.value.knightName))) {}
    }

    override fun onRealNameChanged(name: String) {
        _state.value = _state.value.copy(realName = name)
    }

    override fun onKnightNameChanged(name: String) {
        _state.value = _state.value.copy(knightName = name)
    }

    private fun deduceBuild(): String {
        return when (_state.value.answers.last()) {
            0 -> BERSERKER
            1 -> SHAMAN
            else -> MONK
        }
    }

    override suspend fun updateBuild(): Result<Unit> {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        _state.value = _state.value.copy(build = deduceBuild())
        return handleResultSuspend(repository.updateBuild(PersonalityBuildDTO(_state.value.build))) {
            _uiState.value = UIState.Success(Unit)
            _isLoading.value = false
        }
    }

}
