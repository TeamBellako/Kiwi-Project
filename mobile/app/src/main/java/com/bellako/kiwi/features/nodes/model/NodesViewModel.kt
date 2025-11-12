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
                    val nodesList =
                        repository.getNodes().map {
                            val (x, y) = getNodePositionById(it.id)
                            it.copy(posX = x, posY = y)
                        }
                    _nodes.value = nodesList
                    setUiState(UIState.Idle)
                } catch (e: Exception) {
                    e.printStackTrace()
                    setUiState(mapExceptionToUIState(e))
                } finally {
                    setIsLoading(false)
                }
            }
        }

        fun unlockNode(nodeId: Int) = updateNodeSafe { repository.unlockNode(nodeId) }

        fun completeNode(nodeId: Int, nodeOrder: Int) {
            updateNodeSafe { repository.completeNode(nodeId) }
            updateNodeSafe { repository.markNextNodeAsLocked(nodeId + 1) } // AQUI DEVOLVERA LIST Y TENDRE QUE CAMBIAR LA FUNCION
            // ESTO NO ESTÁ HACIENDO LO QUE YO QUIERO, AHI HABRIA QUE PASAR EL ID DEL NODO DE AHORA
            // Y QUE BUSCASE TODOS LOS NODOS DEL SIGUIENTE ORDER Y LOS DESBLOQUEE
        }

        // -----------------------------------------------------------------------------------------

        private fun updateNodeSafe(block: suspend () -> NodesDomain) {
            viewModelScope.launch {
                setIsLoading(true)
                setUiState(UIState.Loading)
                try {
                    val updatedNode = block()
                    val (x, y) = getNodePositionById(updatedNode.id) // TODO HACK WARRO MIENTRAS NO EXISTE LA POS EN EL BACKEND
                    _nodes.value =
                        _nodes.value.map {
                            if (it.id == updatedNode.id) {
                                updatedNode.copy(posX = x, posY = y)
                            } else {
                                it
                            }
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

        // -----------------------------------------------------------------------------------------

        private fun getNodePositionById(id: Int): Pair<Float, Float> =
            when (id) {
                1 -> 0.5f to 0.6f
                2 -> 0.25f to 0.25f
                3 -> 0.5f to 0.5f
                else -> 0.5f to 0.90f
            }
    }
