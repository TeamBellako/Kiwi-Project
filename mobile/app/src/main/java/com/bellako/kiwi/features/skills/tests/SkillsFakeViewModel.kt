package com.bellako.kiwi.features.skills.tests

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

class SkillsFakeViewModel(
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

    override suspend fun notifySkillLevelUp(skill: SkillDomain) {
        notify(SkillNotificationEvent.SkillLevelUp(skill))
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

        val newSkill = SkillsTestFactory.skill2().copy(id = skillId)

        _state.value =
            _state.value.copy(
                skills = _state.value.skills + newSkill,
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
                skills = _state.value.skills + newSkill,
            )

        handleSuccess()
        setUiState(UIState.Success(Unit))
    }

    // COOLDOWN
    override fun putOnCooldown(skillId: Long) {
        updateSkill(skillId) {
            when (it.cooldownType) {
                CooldownType.TIME ->
                    it.copy(
                        isCooldown = true,
                        cooldownUntil = Instant.now().plusSeconds(60),
                    )

                CooldownType.GOAL,
                CooldownType.OTHER,
                ->
                    it.copy(
                        isCooldown = true,
                        cooldownUntil = null,
                    )
            }
        }
    }

    override fun removeCooldown(skillId: Long) {
        updateSkill(skillId) {
            it.copy(
                isCooldown = false,
                cooldownUntil = null,
            )
        }
    }

    // INTERNAL
    private fun updateSkill(
        skillId: Long,
        transform: (SkillDomain) -> SkillDomain,
    ) {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error updating skill"))
            return
        }

        _state.value =
            _state.value.copy(
                skills =
                    _state.value.skills.map {
                        if (it.id == skillId) transform(it) else it
                    },
            )

        handleSuccess()
        setUiState(UIState.Success(Unit))
    }
}
