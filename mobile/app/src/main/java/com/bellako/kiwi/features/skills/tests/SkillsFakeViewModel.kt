package com.bellako.kiwi.features.skills.tests

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillsState
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.features.skills.screen.SkillNotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

        override fun notify(
            type: SkillNotificationType,
            skill: SkillDomain,
        ) {
            // No needed to test
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
            })
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
                            val target = currentGoal.target
                            val updatedGoal = currentGoal.copy(progress = newProgress)
                            if (newProgress >= target) {
                                skill.copy(goalData = updatedGoal, isCooldown = false)
                            } else {
                                skill.copy(goalData = updatedGoal)
                            }
                        }
                    }
                },
            )
        }

        override suspend fun loadSkills(): Result<Unit> = if (fakeError) Result.failure(fakeException) else Result.success(Unit)

        override suspend fun equipStarterIfNeeded(): Result<Unit> {
            if (fakeError) return Result.failure(fakeException)
            if (_state.value.skills.values.none { it.deckSlot > 0 }) {
                _state.value.skills.values.firstOrNull()?.let { equipSkill(it.id) }
            }
            return Result.success(Unit)
        }

        // INTERNAL
        private fun updateSkill(
            skill: SkillDomain,
            transform: (SkillDomain) -> SkillDomain,
        ) {
            val updated = transform(skill)

            _state.value =
                _state.value.copy(
                    skills = _state.value.skills + (updated.id to updated),
                )
        }
    }
