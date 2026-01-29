package com.bellako.kiwi.features.skills.tests

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.skills.data.CooldownType
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillsState
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.features.skills.model.SkillNotificationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant

class SkillsFakeViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        initialState: SkillsState = SkillsTestFactory.validSkillsState(),
    ) : BaseFakeViewModel(),
        ISkillsViewModel {
        private val _state = MutableStateFlow(initialState)
        override val state: StateFlow<SkillsState> = _state.asStateFlow()

        var fakeError: Boolean = false
        var fakeException: Exception = Exception("Simulated error")

        private val _notifications =
            MutableSharedFlow<SkillNotificationEvent>(
                extraBufferCapacity = 10,
                onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
            )

        override fun getNotifications(): SharedFlow<SkillNotificationEvent> = _notifications.asSharedFlow()

        private suspend fun notify(event: SkillNotificationEvent) {
            _notifications.emit(event)
        }

        override suspend fun notifySkillGiven(skill: SkillDomain) {
            notify(SkillNotificationEvent.SkillGiven(skill))
        }

        override suspend fun notifyCooldownFinished(skill: SkillDomain) {
            notify(SkillNotificationEvent.SkillCooldownFinished(skill))
        }

        // LOAD
        override fun loadAllSkills() {
            if (fakeError) {
                handleError(fakeException)
                setUiState(UIState.Error(fakeException.message ?: "Error loading skills"))
            } else {
                handleSuccess()
                setUiState(UIState.Success(Unit))
            }
        }

        // GIVE
        override fun giveSkill(skillId: Long) {
            if (fakeError) {
                handleError(fakeException)
                setUiState(UIState.Error(fakeException.message ?: "Error giving skill"))
                return
            }

            val newSkill = SkillsTestFactory.skill2()

            _state.value =
                _state.value.copy(
                    skills = _state.value.skills + (newSkill.id to newSkill),
                )

            handleSuccess()
            setUiState(UIState.Success(Unit))
        }

        // LEVEL UP
        override fun levelUpSkill(skillId: Long) {
            if (fakeError) {
                handleError(fakeException)
                setUiState(UIState.Error(fakeException.message ?: "Error leveling skill"))
                return
            }

            val newSkill =
                SkillsTestFactory.skill2()

            _state.value =
                _state.value.copy(
                    skills = _state.value.skills + (newSkill.id to newSkill),
                )

            handleSuccess()
            setUiState(UIState.Success(Unit))
        }

        // COOLDOWN
        @RequiresApi(Build.VERSION_CODES.O)
        @Suppress("MagicNumber")
        override fun putOnCooldown(skillId: Long) {
            val skill = _state.value.skills[skillId] ?: return

            updateSkill(skill, {
                when (it) {
                    is SkillDomain.Goal ->
                        it.copy(
                            isCooldown = true,
                        )
                    is SkillDomain.Other ->
                        it.copy(
                            isCooldown = true,
                        )
                    is SkillDomain.Time ->
                        it.copy(
                            isCooldown = true,
                            cooldownUntil = Instant.now().plusSeconds(60),
                        )
                }
            }, "Error putting skill in cooldown")
        }

        override fun removeCooldown(skillId: Long) {
            val skill = _state.value.skills[skillId] ?: return

            updateSkill(
                skill,
                { skill ->
                    when (skill) {
                        is SkillDomain.Other ->
                            skill.copy(
                                isCooldown = false,
                            )

                        is SkillDomain.Time,
                        is SkillDomain.Goal,
                        ->
                            skill
                    }
                },
                "Error removing cooldown from skill",
            )
        }

        override fun equipSkill(skillId: Long) {
            val skill = _state.value.skills[skillId] ?: return

            updateSkill(
                skill,
                { skill ->
                    when (skill) {
                        is SkillDomain.Other -> skill.copy(deckSlot = 1)
                        is SkillDomain.Time -> skill.copy(deckSlot = 1)
                        is SkillDomain.Goal -> skill.copy(deckSlot = 1)
                    }
                },
                "Error equipping skill",
            )
        }

        override fun unequipSkill(skillId: Long) {
            val skill = _state.value.skills[skillId] ?: return
            updateSkill(
                skill,
                { skill ->
                    when (skill) {
                        is SkillDomain.Other -> skill.copy(deckSlot = 0)
                        is SkillDomain.Time -> skill.copy(deckSlot = 0)
                        is SkillDomain.Goal -> skill.copy(deckSlot = 0)
                    }
                },
                "Error unequipping skill",
            )
        }

        override fun updateGoalProgress(
            skillId: Long,
            goalId: Long,
            newProgress: Int,
        ) {
            val skill = _state.value.skills[skillId] ?: return

            updateSkill(
                skill,
                { skill ->
                    when (skill) {
                        is SkillDomain.Other -> skill
                        is SkillDomain.Time -> skill
                        is SkillDomain.Goal -> {
                            val currentGoal = skill.goalData
                            if (currentGoal != null) {
                                val target = currentGoal.target
                                val updatedGoal = currentGoal.copy(progress = newProgress)
                                if (newProgress >= target) {
                                    skill.copy(goalData = updatedGoal, isCooldown = false)
                                } else {
                                    skill.copy(goalData = updatedGoal)
                                }
                            } else {
                                skill
                            }
                        }
                    }
                },
                "Error update goal progress for skill",
            )
        }

        // INTERNAL
        private fun updateSkill(
            skill: SkillDomain,
            transform: (SkillDomain) -> SkillDomain,
            errorMessage: String,
        ) {
            val updated =
                try {
                    transform(skill)
                } catch (e: Exception) {
                    return
                }

            _state.value =
                _state.value.copy(
                    skills = _state.value.skills + (updated.id to updated),
                )
        }
    }
