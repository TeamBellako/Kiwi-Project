package com.bellako.kiwi.features.personality

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.services.common.BaseViewModel
import com.bellako.kiwi.services.network.AuthRepository
import com.bellako.kiwi.services.common.UIState
import com.bellako.kiwi.types.BERSERKER
import com.bellako.kiwi.types.MONK
import com.bellako.kiwi.types.SHAMAN
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


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

    override fun loadPersonality() {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        viewModelScope.launch(dispatcher) {
            try {
                val result = repository.getPersonality()
                result.fold(
                    onSuccess = { dto ->
                        dto.toDomainObject().onSuccess { domain ->
                            _state.value = domain.toState()
                            previousDomain = domain
                            _uiState.value = UIState.Success(Unit)
                        }.onFailure { ex ->
                            _uiState.value = mapExceptionToUIState(ex)
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.value = mapExceptionToUIState(throwable)
                    }
                )
            } catch (ex: Exception) {
                _uiState.value = mapExceptionToUIState(ex)
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun checkValid(state: PersonalityState): Result<Personality> {
        return state.toDomainObject().fold(
            onSuccess = { validState ->
                Result.success(Personality(validState.realName, validState.knightName, validState.build))
            },
            onFailure = { err ->
                _uiState.value = UIState.Error(err.message.orEmpty())
                Result.failure(err)
            }
        )
    }

    override fun updateRealName(state: PersonalityState) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.updateRealName(PersonalityUserNameDTO(state.realName))
        }
    }

    override fun updateKnightName(state: PersonalityState) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.updateKnightName(PersonalityUserNameDTO(state.knightName))
        }
    }

    override fun onRealNameChanged(name: String) {
        _state.value = _state.value.copy(realName = name)
    }

    override fun onKnightNameChanged(name: String) {
        _state.value = _state.value.copy(knightName = name)
    }

    private fun deduceBuild(state: PersonalityState) {
        _state.value = _state.value.copy(build =
            when (state.answers.last()) {
                0 -> BERSERKER
                1 -> SHAMAN
                else -> MONK
            }
        )
    }

    override suspend fun updateBuild(state: PersonalityState): Result<Unit> {
        _isLoading.value = true
        _uiState.value = UIState.Loading

        deduceBuild(state)
        return handleResultSuspend(repository.updateBuild(PersonalityBuildDTO(state.build))) {
            _uiState.value = UIState.Success(Unit)
            _isLoading.value = false
        }
    }

}
