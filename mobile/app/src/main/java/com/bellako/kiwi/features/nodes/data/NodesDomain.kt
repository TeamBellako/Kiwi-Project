package com.bellako.kiwi.features.nodes.data

data class NodesDomain(
    val id: Int,
    val nodeOrder: Int,
    val status: NodeStatus,
    val price: Int? = null,
    val cordX: Float,
    val cordY: Float,
)

enum class NodeStatus {
    INACCESSIBLE,
    LOCKED,
    OPEN,
    COMPLETED,
}
