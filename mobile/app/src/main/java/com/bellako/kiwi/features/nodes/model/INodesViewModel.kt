package com.bellako.kiwi.features.nodes.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.nodes.data.NodesState

interface INodesViewModel : IBaseViewModel<NodesState> {
    fun loadNodes()

    fun unlockNode(nodeId: Int)

    fun completeNode(nodeId: Int)
}
