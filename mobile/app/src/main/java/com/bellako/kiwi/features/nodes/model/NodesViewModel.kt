package com.bellako.kiwi.features.nodes.model

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.data.NodesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Inject

@HiltViewModel
class NodesViewModel
    @Inject
    constructor(
        private val repository: NodesRepository,
    ) : BaseViewModel(),
        INodesViewModel {
        private val _state = MutableStateFlow(NodesState())
        override val state: StateFlow<NodesState> = _state.asStateFlow()

        // -----------------------------------------------------------------------------------------

        override fun loadNodes() {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val nodes = repository.getNodes()
                    _state.value =
                        _state.value.copy(
                            nodes = nodes.associateBy { it.id },
                        )
                    setUiState(UIState.Idle)
                } catch (e: GeneralSecurityException) {
                    warn("Encryption error: ${e.message}")
                } catch (e: IOException) {
                    warn("DataStore error: ${e.message}")
                } finally {
                    setIsLoading(false)
                }
            }
        }

        override fun unlockNode(nodeId: Long) =
            updateNodesSafe {
                listOf(repository.unlockNode(nodeId))
            }

        override fun completeNode(nodeId: Long) {
            updateNodesSafe {
                repository.completeNode(nodeId)
            }
        }

        // -----------------------------------------------------------------------------------------

        private fun updateNodesSafe(block: suspend () -> List<NodesDomain>) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val updatedNodes = block()
                    val currentNodes = _state.value.nodes.toMutableMap()

                    updatedNodes.forEach { node ->
                        currentNodes[node.id] = node
                    }

                    _state.value =
                        _state.value.copy(
                            nodes = currentNodes,
                        )

                    setUiState(UIState.Success(Unit))
                } catch (e: GeneralSecurityException) {
                    warn("Encryption error: ${e.message}")
                } catch (e: IOException) {
                    warn("DataStore error: ${e.message}")
                } finally {
                    setIsLoading(false)
                }
            }
        }
    }
