package com.bellako.kiwi.features.skills.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.model.GoalsRepository
import com.bellako.kiwi.features.notifications.controller.NotificationEvent
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.screens.QuestNotificationType
import com.bellako.kiwi.features.skills.data.CooldownType
import com.bellako.kiwi.features.skills.data.EquipSkillDTO
import com.bellako.kiwi.features.skills.data.GoalData
import com.bellako.kiwi.features.skills.data.SkillDTO
import com.bellako.kiwi.features.skills.data.SkillDataMapper
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillsState
import com.bellako.kiwi.features.skills.screen.ONE_MINUTE_SECONDS
import com.bellako.kiwi.features.skills.screen.SkillNotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
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

const val ONE_SECOND_MILLISECONDS = 1_000L

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SkillsViewModel
    @Inject
    constructor(
        private val skillsRepository: SkillsRepository,
        private val goalsRepository: GoalsRepository,
        private val notificationManager: NotificationManager,
    ) : BaseViewModel(),
        ISkillsViewModel {
        private val _state = MutableStateFlow(SkillsState())
        override val state: StateFlow<SkillsState> = _state.asStateFlow()

        override fun notify(
            type: SkillNotificationType,
            skill: SkillDomain,
        ) {
            notificationManager.notify(
                NotificationEvent.Skill(
                    type = type,
                    skill = skill,
                ),
            )
        }

        override fun notifySkillGiven(skill: SkillDomain) {
            notify(
                SkillNotificationType.NEW,
                skill,
            )
        }

        override fun notifyCooldownFinished(skill: SkillDomain) {
            notify(
                SkillNotificationType.READY,
                skill,
            )
        }

        // LOAD
        @RequiresApi(Build.VERSION_CODES.O)
        override fun loadAllSkills() {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val skillDTOs = skillsRepository.getAllSkills()

                    val goalsData = loadSkillGoals()
                    val updatedSkills =
                        skillDTOs.map { dto ->
                            if (CooldownType.valueOf(dto.cooldownType) == CooldownType.GOAL) {
                                SkillDataMapper.toGoalDomain(dto, goalsData[dto.cooldownGoalId]!!)
                            } else {
                                SkillDataMapper.toDomainWithoutGoal(dto)
                            }
                        }

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
                    val skill =
                        if (CooldownType.valueOf(dto.cooldownType) == CooldownType.GOAL) {
                            val goalData =
                                goalsRepository
                                    .getGoalById(dto.cooldownGoalId!!)
                                    .getOrThrow()
                                    .toGoalData()

                            SkillDataMapper.toGoalDomain(dto, goalData)
                        } else {
                            SkillDataMapper.toDomainWithoutGoal(dto)
                        }

                    updateSkill(skill)
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

        // LEVEL UP
        @RequiresApi(Build.VERSION_CODES.O)
        override fun levelUpSkill(skillId: Long) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val dto = skillsRepository.levelUpSkill(skillId)
                    val skill =
                        if (CooldownType.valueOf(dto.cooldownType) == CooldownType.GOAL) {
                            val goalData =
                                goalsRepository
                                    .getGoalById(dto.cooldownGoalId!!)
                                    .getOrThrow()
                                    .toGoalData()

                            SkillDataMapper.toGoalDomain(dto, goalData)
                        } else {
                            SkillDataMapper.toDomainWithoutGoal(dto)
                        }

                    updateSkill(skill)
                    notifySkillGiven(skill)

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
                val skillState = _state.value.skills[skillId] ?: return
                val skillDto = cooldownAction(skillId)

                updateSkill(
                    updateCooldown(
                        skillState,
                        skillDto.cooldown,
                        skillDto.cooldownUntil?.let { Instant.ofEpochMilli(it) },
                    ),
                )

                if (!skillDto.cooldown) {
                    notifyCooldownFinished(skillState)
                }

                setUiState(UIState.Success(Unit))
            } catch (e: HttpException) {
                warn("HTTP error on handle cooldown: ${e.message}")
                setUiState(mapExceptionToUIState(e))
            } catch (e: IOException) {
                warn("IO error on handle cooldown: ${e.message}")
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

        // GOAL
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

                    // MAYBE WE SHOULD CREATE A FUNCTION TO UPDATE GOAL VALUE WITHOUT SENDING THE WHOLE GOAL
                    val response =
                        goalsRepository
                            .updateGoal(goal.copy(value = updatedProgress))
                            .getOrNull()
                            ?: return@launch

                    updateSkill(skill.copy(goalData = skill.goalData.copy(progress = response.value)))

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

        // ------------------------------------------------------------------------------------------
        // HELPERS
        // ------------------------------------------------------------------------------------------

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
                is SkillDomain.Goal -> skill.copy(isCooldown = cooldown)
                is SkillDomain.Time ->
                    skill.copy(
                        isCooldown = cooldown,
                        cooldownUntil = cooldownUntil,
                    )
                is SkillDomain.Other -> skill.copy(isCooldown = cooldown)
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

        private suspend fun loadSkillGoals(): Map<Long, GoalData> =
            goalsRepository
                .getSkillGoals()
                .getOrNull()
                ?.associate { it.id to it.toGoalData() }
                ?: emptyMap()

        fun GoalDTO.toGoalData(): GoalData =
            GoalData(
                action = action,
                progress = value,
                target = target,
            )

        init {
            viewModelScope.launch {
                while (true) {
                    delay(ONE_SECOND_MILLISECONDS) // MAYBE WE CAN CHANGE IT TO ONLY UPDATE EVERY MINUTE
                    tickCooldowns()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun tickCooldowns() {
            val now = Instant.now()
            var changed = false

            val updated =
                _state.value.skills.mapValues { (_, skill) ->
                    if (skill is SkillDomain.Time && skill.isCooldown && skill.cooldownUntil != null) {
                        val percentage =
                            timeCooldownPercentage(
                                skill.cooldownUntil,
                                skill.cooldownTimeMinutes,
                                now,
                            )

                        when {
                            percentage >= 1f -> {
                                changed = true
                                notifyCooldownFinished(skill)
                                skill.copy(isCooldown = false, cooldownUntil = null, cooldownProgress = 0f)
                            }

                            percentage != skill.cooldownProgress -> {
                                changed = true
                                skill.copy(cooldownProgress = percentage)
                            }

                            else -> skill
                        }
                    } else {
                        skill
                    }
                }

            if (changed) {
                _state.value = _state.value.copy(skills = updated)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun timeCooldownPercentage(
            cooldownUntil: Instant,
            cooldownTimeMinutes: Int,
            now: Instant,
        ): Float {
            val totalSeconds = cooldownTimeMinutes * ONE_MINUTE_SECONDS
            val remainingSeconds =
                (cooldownUntil.epochSecond - now.epochSecond).coerceAtLeast(0)

            val remainingRatio = remainingSeconds.toFloat() / totalSeconds

            return (1f - remainingRatio).coerceIn(0f, 1f)
        }
    }
