package com.bellako.kiwi.features.nodes.data

object NodesDataMapper {
    fun toDomain(dto: NodesDTO): NodesDomain =
        NodesDomain(
            id = dto.id,
            nodeOrder = dto.nodeOrder,
            status = NodeStatus.valueOf(dto.status),
            price = dto.price,
            cordX = dto.cordX,
            cordY = dto.cordY,
            eventOnExecution = dto.eventOnExecution,
            name = dto.name,
            displayName = dto.displayName,
        )

    fun toDTO(domain: NodesDomain): NodesDTO =
        NodesDTO(
            id = domain.id,
            nodeOrder = domain.nodeOrder,
            status = domain.status.name,
            price = domain.price,
            cordX = domain.cordX,
            cordY = domain.cordY,
            eventOnExecution = domain.eventOnExecution,
            name = domain.name,
            displayName = domain.displayName,
        )
}
