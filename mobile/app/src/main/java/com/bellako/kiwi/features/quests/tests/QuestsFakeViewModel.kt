package com.bellako.kiwi.features.quests.tests

import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestsState
import com.bellako.kiwi.features.quests.data.SubquestStatus
import com.bellako.kiwi.features.quests.model.IQuestsViewModel
import com.bellako.kiwi.features.quests.model.QuestNotificationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class QuestsFakeViewModel(
    initialState: QuestsState = QuestsTestFactory.validQuestsState(),
) : BaseFakeViewModel(),
    IQuestsViewModel {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<QuestsState> = _state.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    // NOTIFICATIONS
    private val _notifications =
        MutableSharedFlow<QuestNotificationEvent>(
            extraBufferCapacity = 10,
            onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
        )

    override fun getNotifications(): SharedFlow<QuestNotificationEvent> = _notifications.asSharedFlow()

    override suspend fun notifyNewQuest(quest: QuestDomain) {
        _notifications.emit(QuestNotificationEvent.NewQuest(quest))
    }

    override suspend fun notifyQuestCompleted(quest: QuestDomain) {
        _notifications.emit(QuestNotificationEvent.QuestCompleted(quest))
    }

    override suspend fun notifySubquestCompleted(
        quest: QuestDomain,
        subquestId: Int,
    ) {
        _notifications.emit(QuestNotificationEvent.SubquestCompleted(quest, subquestId))
    }

    override suspend fun notifySubquestFailed(
        quest: QuestDomain,
        subquestId: Int,
    ) {
        _notifications.emit(QuestNotificationEvent.SubquestFailed(quest, subquestId))
    }

    // LOAD
    override fun loadActiveQuests() {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error loading quests"))
        } else {
            handleSuccess()
            setUiState(UIState.Success(Unit))
        }
    }

    override fun loadCompletedQuests() {
        handleSuccess()
        setUiState(UIState.Success(Unit))
    }

    // GIVE QUEST
    override fun giveQuest(questId: Int) {
        handleSuccess()
        setUiState(UIState.Success(Unit))
    }

    // SUBQUEST ACTIONS
    override fun completeSubquest(subquestId: Int) {
        updateSubquestStatus(subquestId, SubquestStatus.COMPLETED)
    }

    override fun failSubquest(subquestId: Int) {
        updateSubquestStatus(subquestId, SubquestStatus.FAILED)
    }

    // INTERNAL
    private fun updateSubquestStatus(
        subquestId: Int,
        newStatus: SubquestStatus,
    ) {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error updating subquest"))
            return
        }

        val updatedQuests =
            _state.value.quests.map { quest ->
                val updatedSubquests =
                    quest.subquests.map { subquest ->
                        if (subquest.id == subquestId) {
                            subquest.copy(status = newStatus)
                        } else {
                            subquest
                        }
                    }

                quest.copy(subquests = updatedSubquests)
            }

        _state.value = _state.value.copy(quests = updatedQuests)
        handleSuccess()
        setUiState(UIState.Success(Unit))
    }
}
