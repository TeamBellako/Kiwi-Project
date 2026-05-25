package com.bellako.kiwi.features.nodes.data

data class NodesDTO(
    val id: Long,
    val icon: Int,
    val status: String,
    val price: Int,
    val cordX: Float,
    val cordY: Float,
    val name: String,
    val displayName: String,
    val connectedNodeIds: List<Long>,
    val mapId: Int,
    val onExecutionEvent: String,
    val onExecutionEntityId: Int,
    val transitionStyle: String = "VEIL",
)
