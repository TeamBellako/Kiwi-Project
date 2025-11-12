package com.bellako.kiwi.features.nodes.data

data class NodesDTO(
    val id: Int,
    val nodeOrder: Int,
    val status: String,
    val price: Int? = null,
    val cord_x: Float,
    val cord_y: Float,
)
