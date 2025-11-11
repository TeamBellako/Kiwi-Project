package com.bellako.kiwi.features.nodes.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.features.nodes.data.NodesDataMapper
import com.bellako.kiwi.features.nodes.data.NodesDomain

class NodesRepository(
    private val api: INodesAPI,
) {
    suspend fun getNodes(): List<NodesDomain> = api.getNodesForUser().map { NodesDataMapper.toDomain(it) }

    suspend fun getNode(nodeId: Int): NodesDomain = NodesDataMapper.toDomain(api.getNode(nodeId))

    suspend fun unlockNode(nodeId: Int): NodesDomain = NodesDataMapper.toDomain(api.unlockNode(nodeId))

    suspend fun completeNode(nodeId: Int): NodesDomain = NodesDataMapper.toDomain(api.completeNode(nodeId))

    suspend fun markNextNodeAsLocked(nodeId: Int): NodesDomain = NodesDataMapper.toDomain(api.markNextNodeAsLocked(nodeId))
}
