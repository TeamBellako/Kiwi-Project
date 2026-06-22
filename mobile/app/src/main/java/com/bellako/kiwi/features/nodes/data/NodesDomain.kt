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
    val transitionStyle: NodeTransitionStyle = NodeTransitionStyle.VEIL,
)

enum class NodeStatus {
    INACCESSIBLE,
    LOCKED,
    OPEN,
    COMPLETED,
}

// Presentation hint set by content authors: declares how the entry into the
// node's follow-up sequence should be staged. Decoupled from the consequence
// of the node — the veil layer reads this directly, never inspects what the
// event ends up triggering.
enum class NodeTransitionStyle {
    VEIL,
    IMMEDIATE,
}
