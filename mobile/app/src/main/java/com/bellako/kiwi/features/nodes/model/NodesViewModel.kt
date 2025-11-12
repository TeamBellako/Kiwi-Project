package com.bellako.kiwi.features.nodes.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.features.nodes.data.NodesDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NodesViewModel
    @Inject
    constructor(
        private val repository: NodesRepository,
    ) : BaseViewModel() {
        private val _nodes = MutableStateFlow<List<NodesDomain>>(emptyList())
        val nodes: StateFlow<List<NodesDomain>> = _nodes.asStateFlow()

        // -----------------------------------------------------------------------------------------

        fun loadNodes() {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    _nodes.value = repository.getNodes()
                    setUiState(UIState.Idle)
                } catch (e: Exception) {
                    e.printStackTrace()
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        fun unlockNode(nodeId: Int) = updateNodesSafe { listOf(repository.unlockNode(nodeId)) }

        fun completeNode(nodeId: Int) {
            updateNodesSafe { listOf(repository.completeNode(nodeId)) }
            updateNodesSafe { repository.markNextNodesAsLocked(nodeId) }
        }

        // -----------------------------------------------------------------------------------------

        private fun updateNodesSafe(block: suspend () -> List<NodesDomain>) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val updatedNodes = block()

                    _nodes.value =
                        _nodes.value.map { n ->
                            updatedNodes.find { it.id == n.id } ?: n
                        }

                    setUiState(UIState.Success(Unit))
                } catch (e: Exception) {
                    e.printStackTrace()
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }
    }
