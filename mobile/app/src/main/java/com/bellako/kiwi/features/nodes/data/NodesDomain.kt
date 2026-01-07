package com.bellako.kiwi.features.nodes.data

data class NodesDomain(
    val id: Long,
    val icon: Int,
    val status: NodeStatus,
    val price: Int,
    val cordX: Float,
    val cordY: Float,
    val eventOnExecution: Int,
    val name: String,
    val displayName: String,
    val connectedNodeIds: List<Long>,
)

enum class NodeStatus {
    INACCESSIBLE,
    LOCKED,
    OPEN,
    COMPLETED,
}
