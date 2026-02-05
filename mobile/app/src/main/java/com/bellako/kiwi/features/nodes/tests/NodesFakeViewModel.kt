package com.bellako.kiwi.features.nodes.tests

import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.data.NodesState
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NodesFakeViewModel(
    initialState: NodesState = NodesTestFactory.validNodesState(),
) : BaseFakeViewModel(),
    INodesViewModel {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<NodesState> = _state.asStateFlow()

    var fakeError: Boolean = false
    var fakeException: Exception = Exception("Simulated error")

    // ---------------------------------------------------------------------------------------------

    override fun loadNodes() {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error loading nodes"))
        } else {
            handleSuccess()
            setUiState(UIState.Success(Unit))
        }
    }

    // ---------------------------------------------------------------------------------------------

    override fun unlockNode(nodeId: Long) {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error unlocking node"))
            return
        }

        val updatedNodes =
            _state.value.nodes.toMutableMap().apply {
                val node = this[nodeId]
                if (node != null) {
                    this[nodeId] = node.copy(status = NodeStatus.OPEN)
                }
            }

        _state.value = _state.value.copy(nodes = updatedNodes)
        handleSuccess()
        setUiState(UIState.Success(Unit))
    }

    // ---------------------------------------------------------------------------------------------

    override fun completeNode(nodeId: Long) {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error completing node"))
            return
        }

        val currentNodes = _state.value.nodes.toMutableMap()

        val completedNode = currentNodes[nodeId]
        if (completedNode != null) {
            currentNodes[nodeId] = completedNode.copy(status = NodeStatus.COMPLETED)
        }

        completedNode?.connectedNodeIds?.forEach { connectedId ->
            val connectedNode = currentNodes[connectedId]

            if (connectedNode != null) {
                currentNodes[connectedId] = connectedNode.copy(status = connectedNode.status)
            } else {
                val newNode =
                    NodesDomain(
                        id = connectedId,
                        icon = 0,
                        status = NodeStatus.LOCKED,
                        price = 0,
                        cordX = 0f,
                        cordY = 0f,
                        eventOnExecution = 0,
                        name = "Node $connectedId",
                        displayName = "Node $connectedId",
                        connectedNodeIds = emptyList(),
                    )
                currentNodes[connectedId] = newNode
            }
        }

        _state.value = _state.value.copy(nodes = currentNodes)

        handleSuccess()
        setUiState(UIState.Success(Unit))
    }
}
