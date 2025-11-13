package com.bellako.kiwi.features.nodes.tests

import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.model.BaseFakeViewModel
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesState
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NodesFakeViewModel(
    initialState: NodesState = NodesState(),
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
    override fun unlockNode(nodeId: Int) {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error unlocking node"))
            return
        }

        val updatedNodes =
            _state.value.nodes.map { node ->
                if (node.id == nodeId) node.copy(status = NodeStatus.OPEN) else node
            }

        _state.value = _state.value.copy(nodes = updatedNodes)
        handleSuccess()
        setUiState(UIState.Success(Unit))
    }

    // ---------------------------------------------------------------------------------------------
    override fun completeNode(nodeId: Int) {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error completing node"))
            return
        }

        val updatedNodes =
            _state.value.nodes.map { node ->
                if (node.id == nodeId) node.copy(status = NodeStatus.COMPLETED) else node
            }

        _state.value = _state.value.copy(nodes = updatedNodes)
        handleSuccess()
        setUiState(UIState.Success(Unit))
    }

    // ---------------------------------------------------------------------------------------------
    fun markNextNodesAsLocked(nodeId: Int) {
        if (fakeError) {
            handleError(fakeException)
            setUiState(UIState.Error(fakeException.message ?: "Error locking nodes"))
            return
        }

        val updatedNodes =
            _state.value.nodes.map { node ->
                if (node.id > nodeId) node.copy(status = NodeStatus.LOCKED) else node
            }

        _state.value = _state.value.copy(nodes = updatedNodes)
        handleSuccess()
        setUiState(UIState.Success(Unit))
    }
}
