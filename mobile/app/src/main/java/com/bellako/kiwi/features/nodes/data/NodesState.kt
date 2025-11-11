package com.bellako.kiwi.features.nodes.data

data class NodesState(
    val id: Int,
    val order: Int,
    val status: String,
    val price: Int? = null,
    val posX: Float,
    val posY: Float,
)
