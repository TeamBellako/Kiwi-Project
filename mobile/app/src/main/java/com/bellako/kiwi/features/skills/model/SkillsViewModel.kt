package com.bellako.kiwi.features.skills.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SkillsViewModel
    @Inject
    constructor(
        private val repository: SkillsRepository,
    ) : BaseViewModel(),
        ISkillsViewModel {
        private val _state = MutableStateFlow(SkillsState())
        override val state: StateFlow<SkillsState> = _state.asStateFlow()

        private val _notifications = MutableSharedFlow<SkillNotificationEvent>()

        override fun getNotifications(): SharedFlow<SkillNotificationEvent> = _notifications.asSharedFlow()

        private suspend fun notify(event: SkillNotificationEvent) {
            _notifications.emit(event)
        }

        override suspend fun notifySkillGiven(skill: SkillDomain) = notify(SkillNotificationEvent.SkillGiven(skill))

        override suspend fun notifySkillLevelUp(skill: SkillDomain) = notify(SkillNotificationEvent.SkillLevelUp(skill))

        override suspend fun notifyCooldownFinished(skill: SkillDomain) = notify(SkillNotificationEvent.SkillCooldownFinished(skill))

        override fun loadAllSkills() {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val skills = repository.getAllSkills()
                    _state.value = _state.value.copy(skills = skills)
                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error loading skills: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error loading skills: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun giveSkill(skillId: Long) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val skill = repository.giveSkill(skillId)
                    _state.value =
                        _state.value.copy(
                            skills = _state.value.skills + skill,
                        )
                    notifySkillGiven(skill)
                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error giving skill: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error giving skill: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun levelUpSkill(skillId: Long) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val newSkill = repository.levelUpSkill(skillId)
                    _state.value =
                        _state.value.copy(
                            skills = _state.value.skills + newSkill,
                        )
                    notifySkillLevelUp(newSkill)
                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error leveling skill: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error leveling skill: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun putOnCooldown(skillId: Long) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val skill = repository.putOnCooldown(skillId)
                    updateState(skill)
                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error putting skill on cooldown: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error putting skill on cooldown: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun removeCooldown(skillId: Long) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val skill = repository.removeCooldown(skillId)
                    updateState(skill)
                    notifyCooldownFinished(skill)
                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error removing cooldown: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error removing cooldown: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        private fun updateState(skill: SkillDomain) {
            _state.value =
                _state.value.copy(
                    skills =
                        _state.value.skills.map {
                            if (it.id == skill.id) skill else it
                        },
                )
        }
    }
