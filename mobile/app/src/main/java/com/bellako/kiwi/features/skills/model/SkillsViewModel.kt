package com.bellako.kiwi.features.skills.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.model.GoalsRepository
import com.bellako.kiwi.features.skills.data.EquipSkillDTO
import com.bellako.kiwi.features.skills.data.GoalData
import com.bellako.kiwi.features.skills.data.SkillDTO
import com.bellako.kiwi.features.skills.data.SkillDataMapper
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import javax.inject.Inject

const val MAX_DECK_SLOTS = 4

@HiltViewModel
class SkillsViewModel
    @Inject
    constructor(
        private val skillsRepository: SkillsRepository,
        private val goalsRepository: GoalsRepository,
    ) : BaseViewModel(),
        ISkillsViewModel {
        private val _state = MutableStateFlow(SkillsState())
        override val state: StateFlow<SkillsState> = _state.asStateFlow()

        private val cooldownJobs = mutableMapOf<Long, Job>()

        private val _notifications = MutableSharedFlow<SkillNotificationEvent>()

        override fun getNotifications(): SharedFlow<SkillNotificationEvent> = _notifications.asSharedFlow()

        private suspend fun notify(event: SkillNotificationEvent) {
            _notifications.emit(event)
        }

        override suspend fun notifySkillGiven(skill: SkillDomain) = notify(SkillNotificationEvent.SkillGiven(skill))

        override suspend fun notifyCooldownFinished(skill: SkillDomain) = notify(SkillNotificationEvent.SkillCooldownFinished(skill))

        // LOAD
        @RequiresApi(Build.VERSION_CODES.O)
        override fun loadAllSkills() {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val previousSkills = _state.value.allSkills
                    val baseSkillDTOs = skillsRepository.getAllSkills()

                    val goals = loadSkillGoals()
                    val updatedSkills =
                        baseSkillDTOs.map { dto ->
                            val skill = SkillDataMapper.toDomain(dto)
                            if (skill is SkillDomain.Goal) fillSkillGoal(skill, goals[skill.cooldownGoalId]) else skill
                        }

                    detectFinishedCooldowns(
                        previous = previousSkills,
                        current = updatedSkills,
                    )
                    startCooldownTimers(updatedSkills)

                    _state.value =
                        _state.value.copy(
                            skills = updatedSkills.associateBy { it.id },
                        )

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

        // GIVE
        @RequiresApi(Build.VERSION_CODES.O)
        override fun giveSkill(skillId: Long) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val dto = skillsRepository.giveSkill(skillId)
                    val skill = SkillDataMapper.toDomain(dto)
                    val updatedSkill =
                        if (skill is SkillDomain.Goal) {
                            val goal = goalsRepository.getGoalById(skill.cooldownGoalId).getOrNull()
                            fillSkillGoal(skill, goal)
                        } else {
                            skill
                        }

                    updateSkill(updatedSkill)
                    notifySkillGiven(updatedSkill)

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

        // LEVEL UP
        @RequiresApi(Build.VERSION_CODES.O)
        override fun levelUpSkill(skillId: Long) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val dto = skillsRepository.levelUpSkill(skillId)
                    val skill = SkillDataMapper.toDomain(dto)
                    val updatedSkill =
                        if (skill is SkillDomain.Goal) {
                            val goal = goalsRepository.getGoalById(skill.cooldownGoalId).getOrNull()
                            fillSkillGoal(skill, goal)
                        } else {
                            skill
                        }

                    updateSkill(updatedSkill)
                    notifySkillGiven(updatedSkill)

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

        // COOLDOWN
        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun handleCooldown(
            skillId: Long,
            cooldownAction: suspend (Long) -> SkillDTO,
        ) {
            setIsLoading(true)
            setUiState(UIState.Loading)
            try {
                val skill = _state.value.skills[skillId] ?: return
                val dto = cooldownAction(skillId)
                val response = SkillDataMapper.toDomain(dto)

                updateSkill(updateCooldown(skill, response.isCooldown, (response as? SkillDomain.Time)?.cooldownUntil))

                if (!response.isCooldown) notifyCooldownFinished(skill)

                setUiState(UIState.Success(Unit))
            } catch (e: HttpException) {
                warn("HTTP error on cooldown: ${e.message}")
                setUiState(mapExceptionToUIState(e))
            } catch (e: IOException) {
                warn("IO error on cooldown: ${e.message}")
                setUiState(UIState.GeneralError)
            } finally {
                setIsLoading(false)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun putOnCooldown(skillId: Long) {
            viewModelScope.launch {
                handleCooldown(skillId) { skillsRepository.putOnCooldown(it) }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun removeCooldown(skillId: Long) {
            viewModelScope.launch {
                handleCooldown(skillId) { skillsRepository.removeCooldown(it) }
            }
        }

        // EQUIP
        override fun equipSkill(skillId: Long) {
            val deckSlot = emptySlot() ?: return
            val dto = EquipSkillDTO(deckSlot)

            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val skill = _state.value.skills[skillId] ?: return@launch
                    val response = skillsRepository.equipSkill(skillId, dto)

                    updateSkill(updateDeckSlot(skill, response.deckSlot))

                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error equipping skill: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error equipping skill: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun unequipSkill(skillId: Long) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val skill = _state.value.skills[skillId] ?: return@launch
                    val response = skillsRepository.unequipSkill(skillId)

                    updateSkill(updateDeckSlot(skill, response.deckSlot))

                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error unequipping skill: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error unequipping skill: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun updateGoalProgress(
            skillId: Long,
            goalId: Long,
            newProgress: Int,
        ) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val skill =
                        (_state.value.skills[skillId] as? SkillDomain.Goal)
                            ?: return@launch

                    val goal =
                        goalsRepository.getGoalById(goalId).getOrNull()
                            ?: return@launch

                    val updatedProgress =
                        if (newProgress >= goal.target) 0 else newProgress

                    val response =
                        goalsRepository
                            .updateGoal(goal.copy(value = updatedProgress))
                            .getOrNull()
                            ?: return@launch

                    val updatedSkill =
                        skill.copy(
                            goalData =
                                skill.goalData?.copy(
                                    progress = response.value,
                                ),
                        )

                    updateSkill(updatedSkill)

                    if (response.value == 0) {
                        removeCooldown(skillId)
                    }

                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error updating goal progress: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error updating goal progress: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        // HELPERS
        private fun updateSkill(skill: SkillDomain) {
            _state.value =
                _state.value.copy(
                    skills = _state.value.skills + (skill.id to skill),
                )
        }

        private fun updateDeckSlot(
            skill: SkillDomain,
            slot: Int,
        ): SkillDomain =
            when (skill) {
                is SkillDomain.Other -> skill.copy(deckSlot = slot)
                is SkillDomain.Time -> skill.copy(deckSlot = slot)
                is SkillDomain.Goal -> skill.copy(deckSlot = slot)
            }

        private fun updateCooldown(
            skill: SkillDomain,
            cooldown: Boolean,
            cooldownUntil: Instant?,
        ): SkillDomain =
            when (skill) {
                is SkillDomain.Other -> skill.copy(isCooldown = cooldown)
                is SkillDomain.Time ->
                    skill.copy(
                        isCooldown = cooldown,
                        cooldownUntil = cooldownUntil,
                    )
                is SkillDomain.Goal -> skill.copy(isCooldown = cooldown)
            }

        private fun emptySlot(): Int? {
            for (slot in 1..MAX_DECK_SLOTS) {
                if (_state.value.skills.values
                        .none { it.deckSlot == slot }
                ) {
                    return slot
                }
            }
            return null
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun startCooldownTimers(skills: List<SkillDomain>) {
            skills
                .filterIsInstance<SkillDomain.Time>()
                .filter { it.isCooldown && it.cooldownUntil != null }
                .forEach { skill ->
                    if (cooldownJobs.containsKey(skill.id)) return@forEach

                    val job =
                        viewModelScope.launch {
                            val remainingMillis =
                                skill.cooldownUntil!!.toEpochMilli() - Instant.now().toEpochMilli()
                            if (remainingMillis > 0) delay(remainingMillis)
                            loadAllSkills()
                        }

                    cooldownJobs[skill.id] = job
                }
        }

        private fun detectFinishedCooldowns(
            previous: List<SkillDomain>,
            current: List<SkillDomain>,
        ) {
            val previousById = previous.associateBy { it.id }
            val currentById = current.associateBy { it.id }

            previousById.forEach { (id, oldSkill) ->
                val newSkill = currentById[id] ?: return@forEach
                val wasTimedCooldown = oldSkill is SkillDomain.Time && oldSkill.isCooldown
                val isNowNotCooldown = !newSkill.isCooldown
                if (wasTimedCooldown && isNowNotCooldown) {
                    clearCooldownJob(id)
                    viewModelScope.launch { notifyCooldownFinished(newSkill) }
                }
            }
        }

        private fun clearCooldownJob(skillId: Long) {
            cooldownJobs.remove(skillId)?.cancel()
        }

        private suspend fun loadSkillGoals(): Map<Long, GoalDTO> =
            goalsRepository.getSkillGoals().getOrNull()?.associateBy { it.id } ?: emptyMap()

        private fun fillSkillGoal(
            skill: SkillDomain.Goal,
            goal: GoalDTO?,
        ): SkillDomain.Goal =
            if (goal != null) {
                skill.copy(
                    goalData =
                        GoalData(
                            action = goal.action,
                            progress = goal.value,
                            target = goal.target,
                        ),
                )
            } else {
                skill
            }
    }
