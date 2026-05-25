package com.bellako.kiwi.features.nodes.data

object NodesDataMapper {
    fun toDomain(dto: NodesDTO): NodesDomain =
        NodesDomain(
            id = dto.id,
            icon = dto.icon,
            status = NodeStatus.valueOf(dto.status),
            price = dto.price,
            cordX = dto.cordX,
            cordY = dto.cordY,
            name = dto.name,
            displayName = dto.displayName,
            connectedNodeIds = dto.connectedNodeIds,
            mapId = dto.mapId,
            onExecutionEvent = dto.onExecutionEvent,
            onExecutionEntityId = dto.onExecutionEntityId,
            transitionStyle = NodeTransitionStyle.valueOf(dto.transitionStyle),
        )

    fun toDTO(domain: NodesDomain): NodesDTO =
        NodesDTO(
            id = domain.id,
            icon = domain.icon,
            status = domain.status.name,
            price = domain.price,
            cordX = domain.cordX,
            cordY = domain.cordY,
            name = domain.name,
            displayName = domain.displayName,
            connectedNodeIds = domain.connectedNodeIds,
            mapId = domain.mapId,
            onExecutionEvent = domain.onExecutionEvent,
            onExecutionEntityId = domain.onExecutionEntityId,
            transitionStyle = domain.transitionStyle.name,
        )
}
