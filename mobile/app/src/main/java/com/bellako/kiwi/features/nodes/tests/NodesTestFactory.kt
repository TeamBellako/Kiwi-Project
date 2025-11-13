package com.bellako.kiwi.features.nodes.tests

import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.data.NodesState

@Suppress("MagicNumber")
object NodesTestFactory {
    fun validNodesState(): NodesState =
        NodesState(
            nodes =
                listOf(
                    validNodeDomain(1, NodeStatus.COMPLETED, 0.5f, 0.5f, 1),
                    validNodeDomain(2, NodeStatus.COMPLETED, 0.5f, 0.55f, 2),
                    validNodeDomain(3, NodeStatus.OPEN, 0.6f, 0.65f, 3),
                    validNodeDomain(4, NodeStatus.LOCKED, 0.7f, 0.65f, 3),
                    validNodeDomain(4, NodeStatus.INACCESSIBLE, 0.5f, 0.65f, 4),
                ),
        )

    fun validNodeDomain(
        id: Int,
        status: NodeStatus,
        cordX: Float,
        cordY: Float,
        order: Int,
    ): NodesDomain =
        NodesDomain(
            id = id,
            status = status,
            cordX = cordX,
            cordY = cordY,
            nodeOrder = order,
        )
}
