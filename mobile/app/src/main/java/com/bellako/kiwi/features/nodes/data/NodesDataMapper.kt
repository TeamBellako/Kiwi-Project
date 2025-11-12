package com.bellako.kiwi.features.nodes.data

import android.os.Build
import androidx.annotation.RequiresApi

object NodesDataMapper {
    fun toDomain(dto: NodesDTO): NodesDomain =
        NodesDomain(
            id = dto.id,
            nodeOrder = dto.nodeOrder,
            status = NodeStatus.valueOf(dto.status),
            price = dto.price,
            cord_x = dto.cord_x,
            cord_y = dto.cord_y,
        )

    fun toDTO(domain: NodesDomain): NodesDTO =
        NodesDTO(
            id = domain.id,
            nodeOrder = domain.nodeOrder,
            status = domain.status.name,
            price = domain.price,
            cord_x = domain.cord_x,
            cord_y = domain.cord_y,
        )
}
