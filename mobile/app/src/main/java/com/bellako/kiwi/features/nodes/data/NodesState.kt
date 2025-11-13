package com.bellako.kiwi.features.nodes.data

data class NodesState(
    val id: Int,
    val nodeOrder: Int,
    val status: String,
    val price: Int? = null,
    val cordX: Float,
    val cordY: Float,
)
