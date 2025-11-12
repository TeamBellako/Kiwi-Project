package com.bellako.kiwi.features.nodes.data

data class NodesDomain(
    val id: Int,
    val nodeOrder: Int,
    val status: NodeStatus,
    val price: Int? = null,
    val cord_x: Float,
    val cord_y: Float,
)

enum class NodeStatus {
    INACCESSIBLE,
    LOCKED,
    OPEN,
    COMPLETED,
}
