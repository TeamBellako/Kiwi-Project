package com.bellako.kiwi.features.nodes.data

data class NodesDomain(
    val id: Int,
    val order: Int,
    val status: NodeStatus,
    val price: Int? = null,
    val posX: Float,
    val posY: Float,
)

enum class NodeStatus {
    INACCESSIBLE,
    LOCKED,
    OPEN,
    COMPLETED,
    ;

    fun isCompleted() = this == COMPLETED

    fun isAccessible() = this != INACCESSIBLE
}
