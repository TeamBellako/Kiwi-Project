package com.bellako.kiwi.features.quests.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.quests.data.QuestDTO
import com.bellako.kiwi.features.quests.data.QuestDataMapper
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestStatus
import com.bellako.kiwi.features.quests.data.QuestsState
import com.bellako.kiwi.features.quests.data.SubquestResultDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuestNotificationEvent {
    data class NewQuest(
        val quest: QuestDomain,
    ) : QuestNotificationEvent()

    data class QuestCompleted(
        val quest: QuestDomain,
    ) : QuestNotificationEvent()

    data class SubquestCompleted(
        val quest: QuestDomain,
        val subquestId: Int,
    ) : QuestNotificationEvent()

    data class SubquestFailed(
        val quest: QuestDomain,
        val subquestId: Int,
    ) : QuestNotificationEvent()
}

@HiltViewModel
class QuestsViewModel
    @Inject
    constructor(
        private val repository: QuestsRepository,
    ) : BaseViewModel(),
        IQuestsViewModel {
        private val _state = MutableStateFlow(QuestsState())
        override val state: StateFlow<QuestsState> = _state.asStateFlow()

        // NOTIFICATIONS
        private val _notifications = MutableSharedFlow<QuestNotificationEvent>()

        private suspend fun notify(event: QuestNotificationEvent) {
            _notifications.emit(event)
        }

        override fun getNotifications(): SharedFlow<QuestNotificationEvent> = _notifications.asSharedFlow()

        override suspend fun notifyNewQuest(quest: QuestDomain) {
            notify(QuestNotificationEvent.NewQuest(quest))
        }

        override suspend fun notifyQuestCompleted(quest: QuestDomain) {
            notify(QuestNotificationEvent.QuestCompleted(quest))
        }

        override suspend fun notifySubquestCompleted(
            quest: QuestDomain,
            subquestId: Int,
        ) {
            notify(QuestNotificationEvent.SubquestCompleted(quest, subquestId))
        }

        override suspend fun notifySubquestFailed(
            quest: QuestDomain,
            subquestId: Int,
        ) {
            notify(QuestNotificationEvent.SubquestFailed(quest, subquestId))
        }

        // LOAD QUESTS
        override fun loadActiveQuests() {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val quests = repository.getActiveQuests()
                    _state.value = _state.value.copy(quests = quests)
                    setUiState(UIState.Idle)
                } catch (e: Exception) {
                    warn("Error loading active quests: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun loadCompletedQuests() {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val quests = repository.getCompletedQuests()
                    _state.value = _state.value.copy(quests = quests)
                    setUiState(UIState.Idle)
                } catch (e: Exception) {
                    warn("Error loading completed quests: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        // GIVE QUEST
        override fun giveQuest(questId: Int) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val quest = repository.giveQuest(questId)
                    _state.value =
                        _state.value.copy(
                            quests = _state.value.quests + quest,
                        )
                    notifyNewQuest(quest)
                    setUiState(UIState.Success(Unit))
                } catch (e: Exception) {
                    warn("Error giving quest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        // SUBQUEST COMPLETE
        override fun completeSubquest(subquestId: Int) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val questUpdated = repository.completeSubquest(subquestId)
                    updateState(questUpdated)

                    notifySubquestCompleted(QuestDataMapper.toDomain(questUpdated), subquestId)
                    if (questUpdated.status == QuestStatus.COMPLETED.toString()) {
                        notifyQuestCompleted(QuestDataMapper.toDomain(questUpdated))
                    }

                    setUiState(UIState.Success(Unit))
                } catch (e: Exception) {
                    warn("Error completing subquest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        // SUBQUEST COMPLETE / FAIL
        override fun failSubquest(subquestId: Int) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val questUpdated = repository.failSubquest(subquestId)
                    updateState(questUpdated)

                    notifySubquestFailed(QuestDataMapper.toDomain(questUpdated), subquestId)
                    if (questUpdated.status == QuestStatus.COMPLETED.toString()) {
                        notifyQuestCompleted(QuestDataMapper.toDomain(questUpdated))
                    }
                    setUiState(UIState.Success(Unit))
                } catch (e: Exception) {
                    warn("Error failing subquest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        // STATE UPDATE
        private fun updateState(dto: QuestDTO) {
            val updatedQuests =
                _state.value.quests.map { quest ->
                    if (quest.id == dto.questId) {
                        QuestDataMapper.toDomain(dto)
                    } else {
                        quest
                    }
                }

            _state.value = _state.value.copy(quests = updatedQuests)
        }
    }
