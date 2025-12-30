package com.bellako.kiwi.features.nodes.data

data class NodesDTO(
    val id: Int,
    val nodeOrder: Int,
    val status: String,
    val price: Int,
    val cordX: Float,
    val cordY: Float,
    val eventOnExecution: Int,
    val name: String,
    val displayName: String,
)
