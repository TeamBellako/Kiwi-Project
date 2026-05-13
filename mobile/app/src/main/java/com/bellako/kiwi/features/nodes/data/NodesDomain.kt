package com.bellako.kiwi.features.nodes.data

data class NodesDomain(
    val id: Long,
    val icon: Int,
    val status: NodeStatus,
    val price: Int,
    val cordX: Float,
    val cordY: Float,
    val name: String,
    val displayName: String,
    val connectedNodeIds: List<Long>,
    val mapId: Int,
    val onExecutionEvent: String,
    val onExecutionEntityId: Int,
)

enum class NodeStatus {
    INACCESSIBLE,
    LOCKED,
    OPEN,
    COMPLETED,
}
