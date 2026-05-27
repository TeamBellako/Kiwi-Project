package com.bellako.kiwi.features.nodes.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.data.NodesState

interface INodesViewModel : IBaseViewModel<NodesState> {
    fun loadNodes(mapId: Int)

    fun unlockNode(nodeId: Long)

    fun completeNode(nodeId: Long)

    // Fresh-account hand-off: load the first map's nodes, and if the user has
    // no progress yet (no OPEN/COMPLETED), unlock + complete the first node so
    // the opening beat fires. Suspends until the round-trip is done, so the
    // sign-up flow can call this under the loading curtain and only navigate
    // to the map once the state is final. Returns the completed node (with
    // its onExecutionEvent info) for the caller to emit, or null if no
    // auto-execution was performed.
    suspend fun autoExecuteFirstNode(mapId: Int): NodesDomain?
}
