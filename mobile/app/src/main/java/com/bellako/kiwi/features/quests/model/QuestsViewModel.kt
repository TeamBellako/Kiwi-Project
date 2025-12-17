package com.bellako.kiwi.features.quests.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.quests.data.QuestDataMapper
import com.bellako.kiwi.features.quests.data.QuestsState
import com.bellako.kiwi.features.quests.data.SubquestResultDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QuestsViewModel
    @Inject
    constructor(
        private val repository: QuestsRepository,
    ) : BaseViewModel(),
        IQuestsViewModel {
        private val _state = MutableStateFlow(QuestsState())
        override val state: StateFlow<QuestsState> = _state.asStateFlow()

        // =============================================================================================
        // LOAD QUESTS
        // =============================================================================================

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

        // =============================================================================================
        // GIVE QUEST
        // =============================================================================================

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
                    setUiState(UIState.Success(Unit))
                } catch (e: Exception) {
                    warn("Error giving quest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        // =============================================================================================
        // SUBQUEST COMPLETE / FAIL
        // =============================================================================================

        override fun completeSubquest(subquestId: Int) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val result = repository.completeSubquest(subquestId)
                    updateStateFromSubquestResult(result)
                    setUiState(UIState.Success(Unit))
                } catch (e: Exception) {
                    warn("Error completing subquest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
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
                    val result = repository.failSubquest(subquestId)
                    updateStateFromSubquestResult(result)
                    setUiState(UIState.Success(Unit))
                } catch (e: Exception) {
                    warn("Error failing subquest: ${e.message}")
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        // =============================================================================================
        // STATE UPDATE
        // =============================================================================================

        private fun updateStateFromSubquestResult(dto: SubquestResultDTO) {
            val updatedSubquest =
                dto.updatedSubquest?.let { QuestDataMapper.toDomain(it) }
                    ?: return

            val nextSubquest = dto.nextSubquest?.let { QuestDataMapper.toDomain(it) }
            val completedQuest = dto.completedQuest?.let { QuestDataMapper.toDomain(it) }

            val updatedQuests =
                _state.value.quests.map { quest ->

                    if (quest.id != updatedSubquest.id) return@map quest

                    if (completedQuest != null && completedQuest.id == quest.id) {
                        return@map completedQuest
                    }

                    val subquests =
                        quest.subquests.map { s ->
                            when (s.id) {
                                updatedSubquest.id -> updatedSubquest
                                nextSubquest?.id -> nextSubquest
                                else -> s
                            }
                        }

                    quest.copy(subquests = subquests)
                }

            _state.value = _state.value.copy(quests = updatedQuests)
        }
    }
