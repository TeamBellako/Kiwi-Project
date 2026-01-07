package com.bellako.kiwi.features.nodes.tests

import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.data.NodesState

@Suppress("MagicNumber")
object NodesTestFactory {
    fun validNodesState(): NodesState =
        NodesState(
            nodes =
                mapOf(
                    Pair(1L, validNodeDomain(1L, NodeStatus.COMPLETED, 0.5f, 0.5f, 1, 100, 1, "node1", "Node 1", listOf(2L))),
                    Pair(2L, validNodeDomain(2L, NodeStatus.COMPLETED, 0.5f, 0.55f, 2, 100, 2, "node2", "", listOf(3L))),
                    Pair(3L, validNodeDomain(3L, NodeStatus.OPEN, 0.6f, 0.65f, 3, 100, 4, "node3", "", listOf(4L))),
                    Pair(4L, validNodeDomain(4L, NodeStatus.LOCKED, 0.7f, 0.65f, 3, 100, 5, "node4", "", listOf())),
                ),
        )

    fun validNodeDomain(
        id: Long,
        status: NodeStatus,
        cordX: Float,
        cordY: Float,
        icon: Int,
        price: Int,
        eventOnExecution: Int,
        name: String,
        displayName: String,
        connectedNodeIds: List<Long>,
    ): NodesDomain =
        NodesDomain(
            id = id,
            status = status,
            cordX = cordX,
            cordY = cordY,
            icon = icon,
            price = price,
            eventOnExecution = eventOnExecution,
            name = name,
            displayName = displayName,
            connectedNodeIds = connectedNodeIds,
        )
}
