package com.bellako.kiwi.features.nodes.model

import com.bellako.kiwi.features.nodes.data.NodesDataMapper
import com.bellako.kiwi.features.nodes.data.NodesDomain

class NodesRepository(
    private val api: INodesAPI,
) {
    suspend fun getNodesByMapId(mapId: Int): List<NodesDomain> = api.getNodesForMapId(mapId).map { NodesDataMapper.toDomain(it) }

    suspend fun unlockNode(nodeId: Long): NodesDomain = NodesDataMapper.toDomain(api.unlockNode(nodeId))

    suspend fun completeNode(nodeId: Long): List<NodesDomain> = api.completeNode(nodeId).map { NodesDataMapper.toDomain(it) }
}
