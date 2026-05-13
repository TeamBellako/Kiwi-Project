package com.bellako.kiwi.features.nodes.data

data class NodesState(
    val nodes: Map<Long, NodesDomain> = emptyMap<Long, NodesDomain>(),
)
