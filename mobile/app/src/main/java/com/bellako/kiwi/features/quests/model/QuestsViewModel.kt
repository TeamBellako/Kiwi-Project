package com.bellako.kiwi.features.quests.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.notifications.controller.NotificationEvent
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.quests.data.QuestDTO
import com.bellako.kiwi.features.quests.data.QuestDataMapper
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestStatus
import com.bellako.kiwi.features.quests.data.QuestsState
import com.bellako.kiwi.features.quests.data.SubquestStatus
import com.bellako.kiwi.features.quests.screens.QuestNotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
class QuestsViewModel
    @Inject
    constructor(
        private val repository: QuestsRepository,
        private val notificationManager: NotificationManager,
    ) : BaseViewModel(),
        IQuestsViewModel {
        private val _state = MutableStateFlow(QuestsState())
        override val state: StateFlow<QuestsState> = _state.asStateFlow()

        init {
            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.START_QUEST) { eventPayload ->
                    val payload = eventPayload as EventPayload.EntityIdPayload
                    giveQuest(payload.targetEntityId)
                }
            }

            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.COMPLETE_QUEST) { eventPayload ->
                    val payload = eventPayload as EventPayload.EntityIdPayload
                    completeSubquest(payload.targetEntityId)
                }
            }
        }

        private fun notify(
            type: QuestNotificationType,
            quest: QuestDomain,
            subquestId: Int? = null,
        ) {
            notificationManager.notify(
                NotificationEvent.Quest(
                    type = type,
                    quest = quest,
                    subquestId = subquestId,
                ),
            )
        }

        override fun notifyNewQuest(quest: QuestDomain) {
            notify(
                QuestNotificationType.NEW,
                quest,
            )
        }

        override fun notifyQuestCompleted(quest: QuestDomain) {
            notify(
                QuestNotificationType.QUEST_COMPLETED,
                quest,
            )
        }

        override fun notifySubquestCompleted(
            quest: QuestDomain,
            subquestId: Int,
        ) {
            notify(
                QuestNotificationType.SUBQUEST_COMPLETED,
                quest,
                subquestId,
            )
        }

        override fun notifySubquestFailed(
            quest: QuestDomain,
            subquestId: Int,
        ) {
            notify(
                QuestNotificationType.SUBQUEST_FAILED,
                quest,
                subquestId,
            )
        }

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

                    EventBus.emitEvent(EventType.QUESTS_UPDATED, EventPayload.EmptyPayload())
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

                    EventBus.emitEvent(EventType.QUESTS_UPDATED, EventPayload.EmptyPayload())

                    if (domainQuest.onCompletedEvent != "_") {
                        EventBus.emitEvent(
                            EventType.valueOf(domainQuest.onCompletedEvent),
                            EventPayload.EntityIdPayload(domainQuest.onCompletedEntityId),
                        )
                    }
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

                    EventBus.emitEvent(EventType.QUESTS_UPDATED, EventPayload.EmptyPayload())
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
