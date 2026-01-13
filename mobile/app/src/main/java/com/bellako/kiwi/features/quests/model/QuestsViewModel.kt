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
import com.bellako.kiwi.features.quests.data.SubquestStatus
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

        private val _notifications = MutableSharedFlow<QuestNotificationEvent>()

        override fun getNotifications(): SharedFlow<QuestNotificationEvent> = _notifications.asSharedFlow()

        private suspend fun notify(event: QuestNotificationEvent) {
            _notifications.emit(event)
        }

        override suspend fun notifyNewQuest(quest: QuestDomain) = notify(QuestNotificationEvent.NewQuest(quest))

        override suspend fun notifyQuestCompleted(quest: QuestDomain) = notify(QuestNotificationEvent.QuestCompleted(quest))

        override suspend fun notifySubquestCompleted(
            quest: QuestDomain,
            subquestId: Int,
        ) = notify(QuestNotificationEvent.SubquestCompleted(quest, subquestId))

        override suspend fun notifySubquestFailed(
            quest: QuestDomain,
            subquestId: Int,
        ) = notify(QuestNotificationEvent.SubquestFailed(quest, subquestId))

        override fun loadActiveQuests() {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val quests = repository.getActiveQuests()
                    _state.value = _state.value.copy(quests = quests)
                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error loading active quests: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error loading active quests: ${e.message}")
                    setUiState(UIState.GeneralError)
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
                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error loading completed quests: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error loading completed quests: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override suspend fun isQuestCompleted(questId: Int): Boolean {
            try {
                return repository.getQuest(questId).status == QuestStatus.COMPLETED
            } catch (e: HttpException) {
                warn("HTTP error checking quest $questId completion: ${e.message}")
            } catch (e: IOException) {
                warn("IO error checking quest $questId completion: ${e.message}")
            }
            return false
        }

        override suspend fun isSubquestCompleted(subquestId: Int): Boolean {
            try {
                return repository.getSubquest(subquestId).status == SubquestStatus.COMPLETED
            } catch (e: HttpException) {
                warn("HTTP error checking subquest $subquestId completion: ${e.message}")
            } catch (e: IOException) {
                warn("IO error checking subquest $subquestId completion: ${e.message}")
            }
            return false
        }

        override suspend fun isSubquestFailed(subquestId: Int): Boolean {
            try {
                return repository.getSubquest(subquestId).status == SubquestStatus.FAILED
            } catch (e: HttpException) {
                warn("HTTP error checking subquest $subquestId failure: ${e.message}")
            } catch (e: IOException) {
                warn("IO error checking subquest $subquestId failure: ${e.message}")
            }
            return false
        }

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
                } catch (e: HttpException) {
                    warn("HTTP error giving quest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error giving quest: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun completeSubquest(subquestId: Int) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val questUpdated = repository.completeSubquest(subquestId)
                    updateState(questUpdated)

                    val domainQuest = QuestDataMapper.toDomain(questUpdated)
                    notifySubquestCompleted(domainQuest, subquestId)

                    if (questUpdated.status == QuestStatus.COMPLETED.toString()) {
                        notifyQuestCompleted(domainQuest)
                    }

                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error completing subquest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error completing subquest: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun failSubquest(subquestId: Int) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val questUpdated = repository.failSubquest(subquestId)
                    updateState(questUpdated)

                    val domainQuest = QuestDataMapper.toDomain(questUpdated)
                    notifySubquestFailed(domainQuest, subquestId)

                    if (questUpdated.status == QuestStatus.COMPLETED.toString()) {
                        notifyQuestCompleted(domainQuest)
                    }

                    setUiState(UIState.Success(Unit))
                } catch (e: HttpException) {
                    warn("HTTP error failing subquest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } catch (e: IOException) {
                    warn("IO error failing subquest: ${e.message}")
                    setUiState(UIState.GeneralError)
                } finally {
                    setIsLoading(false)
                }
            }
        }

        private fun updateState(dto: QuestDTO) {
            _state.value =
                _state.value.copy(
                    quests =
                        _state.value.quests.map { quest ->
                            if (quest.id == dto.questId) {
                                QuestDataMapper.toDomain(dto)
                            } else {
                                quest
                            }
                        },
                )
        }
    }
